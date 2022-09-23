## 引言

# 
名词解释 \
并行：

名词和缩写
Channel
ChannelPipeline CP
ChannelConfig CC
ChannelFuture CF
ChannelHandler CH
ChannelInboundHandler CIH
ChannelOutboundHandler COH
ChannelHandlerContext ctx

EventLoop loop
EventLoopGroup group


## 1. BIO实例（对应原书第1章）

## 附录A. Netty概念汇总
+ Future。Netty的**异步编程模型**的基础是Future和Callback。
Netty结合这两者构建了ChannelFuture：`public interface ChannelFuture extends Future<Void>`。
它是一个无返回值的可加监听器的Future。
+ ChannelHandler。摘自源码注释：处理 I/O 事件或拦截 I/O 操作，
并将其转发到其ChannelPipeline中的下一个处理程序。如果存在注解@Sharable，它可以在多线程间复用。
+ Selector。IO多路复用核心。
+ Event。
+ EventLoop。有序的事件集合。一个EventLoop对应单线程，因此不存在同步问题。

## 2. Netty demo: Echo （对应原书第2章）
### 3.1 EchoServerHandler
首先组装基本元件——ChannelHandler。并一一拆解
源码位置：./echo/EchoServerHandler.java
```java
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println(
                "Server received: " + in.toString(CharsetUtil.UTF_8));
        ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
        Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```
#### 3.1.1 @Sharable
来自ChannelHandler（CH）的注解，被用于CH实现类上，表示它可以在多个Channel间复用。
ChannelHandlerAdapter进行了这个注解的处理。
```java
public abstract class ChannelHandlerAdapter implements ChannelHandler {
  // ...
  /**
   * Return {@code true} if the implementation is {@link io.netty.channel.ChannelHandler.Sharable} and so can be added
   * to different {@link io.netty.channel.ChannelPipeline}s.
   */
  public boolean isSharable() {
    /**
     * Cache the result of {@link Sharable} annotation detection to workaround a condition. We use a
     * {@link ThreadLocal} and {@link WeakHashMap} to eliminate the volatile write/reads. Using different
     * {@link WeakHashMap} instances per {@link Thread} is good enough for us and the number of
     * {@link Thread}s are quite limited anyway.
     *
     * See <a href="https://github.com/netty/netty/issues/2289">#2289</a>.
     */
    Class<?> clazz = getClass();
    Map<Class<?>, Boolean> cache = InternalThreadLocalMap.get().handlerSharableCache();
    Boolean sharable = cache.get(clazz);
    if (sharable == null) {
      sharable = clazz.isAnnotationPresent(Sharable.class);
      cache.put(clazz, sharable);
    }
    return sharable;
  }
  // ...
}
```
#### 3.1.2 ChannelInboundHandlerAdapter
ChannelInboundHandler（CIH）的实现。
CIH声明了通过ChannelHandlerContext（ctx）交互的入站操作。
ChannelInboundHandlerAdapter（CIHA）调用了ctx相应的入站方法，
除此之外继承了ChannelHandlerAdapter的@Sharable处理方法。
#### 3.1.3 ChannelHandlerContext
CTX继承了3个接口
+ AttributeMap。用于保存状态。
+ ChannelInboundInvoker（CII）。入站方法。
+ ChannelOutboundInvoker（COI）。出站方法。

Q:这里，入站处理器同时调用了入站和出站方法，而且一个CH就刷掉了消息。后续处理器看到的是什么消息呢？
#### 3.1.4 方法
channelRead 入站ByteBuf读事件。
channelReadComplete 入站ByteBuf读完事件。
exceptionCaught 异常事件。
write 写但不刷
writeAndFlush 写和刷，返回ChannelFuture，可加listener
#### 3.1.5 顺一遍
有读事件时写到输出流，读完时刷掉，并加监听器，在刷完时关闭Channel。有异常时打印错误日志并关闭Channel。
### 3.2 EchoServer
```java
public class EchoServer {
  public void start() throws Exception {
    EchoServerHandler sharableHandler = new EchoServerHandler();
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      ServerBootstrap boot = new ServerBootstrap();
      boot.group(group)
          .channel(NioServerSocketChannel.class)
          .localAddress(new InetSocketAddress(Configuration.port))
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(sharableHandler);
            }
          });
      ChannelFuture f = boot.bind().sync();
      System.out.println(EchoServer.class.getName() +
          " started and listening for connections on " + f.channel().localAddress());
      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }
  public static void main(String[] args) throws Exception {
    new EchoServer().start();
  }
}
```
#### 3.2.1 EventLoopGroup
用于接收和处理新连接。
#### 3.2.2 NioServerSocketChannel
不知道，以后再补充
#### 3.2.3 ChannelInitializer<C extends Channel>
不知道
#### 顺一遍

### 3.3 EchoClientHandler
```java
@Sharable
public class EchoClientHandler
    extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!",
                CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        System.out.println(
                "Client received: " + in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
        Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```
和EchoServerHandler逻辑类似，不再分析或以后分析
### 3.4 EchoClient

## 3. Netty组件和设计（对应原书第3章）
### 3.1 Channel
简单来说。Channel就是异步化功能增强版Socket（需要写个demo佐证一下）。
其它的，可以直接看源码注释。
```java
/**
 * {@link io.netty.channel.Channel}
 * {@link java.net.Socket}
 */
```
### 3.2 EventLoop
Channel、EventLoop（loop）、Thread和EventLoopGroup（group）的关系：
+ group一对多loop
+ 一个loop属于一个Thread
+ 一个Channel属于一个loop
+ 多个Channel可以共享一个loop

这似乎意味着，group在给Channel分配loop的时候，可能不会区分loop是否有活在身上。
还是得看源码注释。
NioEventLoop上写了一句很应景的注释：SingleThreadEventLoop实现将Channel
注册到Selector并且在事件循环中对这些进行多路复用。 而NioEventLoop正好继承自
SingleThreadEventLoop。\
但是进来就这么一句：`promise.channel().unsafe().register(this, promise);`
又跑到接口方法上面去了（除了debug就没什么办法定位接口方法了吗。。）。
register方法的实现最终在AbstractUnsafe里找到。

相关源码链接
```java
/**
 * {@link io.netty.channel.EventLoop}
 * {@link io.netty.channel.nio.NioEventLoop}
 * {@link io.netty.channel.AbstractChannel.AbstractUnsafe}
 */
```
### 3.3 ChannelFuture
摘自源码注释：处理 I/O 事件或拦截 I/O 操作，
并将其转发到其ChannelPipeline中的下一个处理程序。如果存在注解@Sharable，它可以在多线程间复用。
```java
/**
 * {@link io.netty.channel.CompleteChannelFuture}
 */
```

### 3.4 ChannelHandler
2.1.2和2.1.3已经讨论过CH及其子类。这里验证了几个关键点。
+ CH是开发主要关心的部分
+ CIH（入站CH）可以直接刷数据作为响应。
+ CH负责把事件转发到下一个CH

Q：可以一个CH处理中途触发下一个CH吗？
### 3.5 ChannelPipeline
ChannelPipeline（CP）：CH链的容器。
Channel创建时被分配到专属CP：ChannelInitializer（CI）注册到ServerBootstrap（boot）中，CH::initChannel在CP中安装CH，最后CI把自己从CP中移出。
一个CH添加到CP时，会被分配（专属的？）ctx，代表着CH和CP的绑定。

Netty的两种发送消息方式：
+ 写到Channel中，这将导致消息从CP的尾端开始流动
+ 写到ctx中，这将导致消息从CP的下一个CH开始流动


Q：验证并发场景下，各种对象被创建的次数。

### 3.6 编码器和解码器
它们都是CH子类型。
解码器在读操作前通过
`ChannelInboundHandler::channelRead`重写已经被调用。
Q：找个调用重写方法的例子debug一下

### 3.7 SimpleChannelInboundHandler
继承这个类并在泛型中指明接收的类型。（字节流转成接收类型的过程没有看到）
重写`SimpleChannelInboundHandler::channelRead0`并保证不会阻塞它。

## 4.传输
### 4.1 有了Bootstrap，用什么传输都差不多（换了几个顶层API）
### 4.2 Channel
+ Channel线程安全，Comparable
+ 每个Channel会分配一个CP和ChannelConfig（CC），以及loop
### 4.3 NIO
非阻塞IO
### 4.4 Epoll
Linux可用的本地非阻塞传输
### 4.5 OIO
阻塞IO
### 4.6 Local
JVM内部异步通信
### 4.7 Embedded
模拟读写事件的单元测试工具

以上，NIO，Epoll，OIO都是支持TCP和UDP的。
## 5. 数据容器ByteBuf
### 5.1 结构
ByteBuf（buf）内部有两个索引，分别指向读位置和写位置。
`ByteBuf::readXXX`，`ByteBuf::skipXXX`和`ByteBuf::writeXXX`会推动索引位置。
`ByteBuf::getXXX`和`ByteBuf::setXXX`则不会。
另一个区别是方法是否有一个索引作为参数。进一步的使用方式参考5.3
### 5.2 使用模式
+ 堆缓冲区（支撑数组，heapBuf）。存储在JVM堆，
+ 直接缓冲区（directBuf）。
+ 复合缓冲区。它的类型不一样（CompositeByteBuf），api和集合差不多，但它本身是可以读写的。
```java
/**
 * {@link io.netty.buffer.CompositeByteBuf}
 */
```
### 5.3 使用
`ByteBuf::readerIndex`和`ByteBuf::writeIndex`可以手动移动索引。
丢弃字节：`ByteBuf::discardReadBytes`回收readerIndex前面的部分（可丢弃字节），
减少readerIndex和writerIndex，把回收的区域扩展到writerIndex右侧的部分（可写字节），
capacity不变。
注意：不要频繁使用它，因为可能会导致内存复制。
管理索引：`ByteBuf::markXXX`和`ByteBuf::resetXXX`用于标记和重置索引到标记。
`ByteBuf::mark`有一个int参数用于指定标记失效条件。
查找：`ByteBuf::indexOf`和`ByteBuf::forEachByte`
ByteBuf视图：视图维护了独立的索引和标记。创建视图的方法如下，注意修改视图即修改源实例。
+ `ByteBuf::duplicate`
+ `ByteBuf::slice`
+ `Unpooled.unmodifiableBuffer`
+ `ByteBuf::readSlice`
ByteBuf复制：创建新的实例。创建副本的方法如下
+ `ByteBuf::copy`
### 5.4 ByteBufHolder
它被设计为适合兼容格式化数据的接口。虽然这个接口看不出来，但是它的实现类看上去重要的多。
这些类如下，合适时再去探究其作用。
```java
/**
 * {@link io.netty.buffer.ByteBufHolder}
 * {@link io.netty.handler.codec.http.multipart.AbstractHttpData}
 */
```
### 5.5 ByteBuf分配
ByteBufAllocator（BBA）实现了ByteBuf的池化（？），可以用来分配各种ByteBuf。
可以通过Channel或ctx获取到BBA的引用。
和ByteBuf分配相关的类如下。
```java
/**
 * {@link io.netty.buffer.ByteBufAllocator}
 * {@link io.netty.buffer.PooledByteBufAllocator}
 * {@link io.netty.buffer.UnpooledByteBufAllocator}
 *
 * {@link io.netty.buffer.Unpooled}
 *
 * {@link io.netty.buffer.ByteBufUtil}
 */
```

## 6. ChannelHandler和ChannelPipeline
### 6.1 Channel生命周期
Channel生命周期按顺序如下：
+ 已注册loop
+ 活跃：连接到远程节点，可以收发数据
+ 不活跃：没有连接到远程节点
+ 注销loop


### 6.2 ChannelHandler生命周期
CH生命周期节点：CH被添加到CP或者从CP中移出。在节点上调用的操作有：
+ handlerAdded：CH添加到CP中
+ handlerRemoved：CH从CP中移出
+ exceptionCaught：CP的处理过程（大概是指handle方法）有错误
### 6.3 ChannelInboundHandler
CIH定义了**数据流入，或Channel状态发生变化**的回调，这些方法的参数往往是**上下文和需要处理的数据**。
关于6.2和6.3的解释可以看如下代码：
```java
/**
 * {@link MyCIH}
 */
```
你可能还关心SimpleChannelInboundHandler。
```java
public class SimpleDiscardHandler
    extends SimpleChannelInboundHandler<Object> {
  @Override
  public void channelRead0(ChannelHandlerContext ctx,
                           Object msg) {
    // No need to do anything special
  }
}
```
### 6.4 ChannelOutboundHandler
COH定义了**Channel，CP，ctx调用**的方法。这些方法的参数往往是**ctx，绑定、连接地址，ChannelPromise**。
```java
/**
 * {@link MyCOH}
 */
```

### 6.5 CH Adapter
别用MyCIH和MyCOH！
Adapter的使用可以看echo包下的demo

### 6.6 CP
手动操作的，线程安全，有序的CH容器，可以触发下一个handler的预设方法（指已经编写在两个invoker里的方法），
以确保事件有可能流经所有handler。其他的看CP类注释：
```java
/**
 * {@link io.netty.channel.ChannelPipeline}
 */
```

### 6.7 ctx
ctx代表了CH和CP的关联。 ctx的事件方法和Channel、CP类似，
区别是后者沿着整个CP传播，ctx方法只会传到下一个handler。
因此ctx往往能获得更佳的性能。
想要从特定handler开始处理，就要获取它前一个handler的ctx。
缓存ctx到实例可以让handler实例拥有访问ctx的能力
（这看上去很容易出现线程安全问题）。
### 6.8 处理异常
入站异常在最后一个CIH上加捕获异常方法。
出站异常可以加监听器，并通过
```java
/**
 * {@link io.netty.channel.ChannelPromise#setFailure(java.lang.Throwable)}
 */
```
主动通知失败。

## 7. EventLoop及线程模型
一个loop永久绑定一个thread。可能会有多个loop实例，
一个loop可能服务多个channel（对于OIO则保证一个loop同时只服务一个channel）。
Runnable和Callable任务可以交给loop立即执行或者调度执行。
（channel事件触发loop还是任务触发？）
线程管理：如果调用线程正是loop绑定的线程，则立即执行，否则调度执行（仅仅是避免线程安全问题吗？）。

## 8. 引导
channel和group的兼容性：分别在nio包和oio包下，不能混用。

## 9. 单元测试
后面的部分需要时再看吧。


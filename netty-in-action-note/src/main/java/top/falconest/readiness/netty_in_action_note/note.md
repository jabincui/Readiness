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
ChannelInboundHandlerAdapter调用了ctx相应的入栈方法，
除此之外继承了ChannelHandlerAdapter的@Sharable处理方法。
#### 3.1.3 ChannelHandlerContext
CTX继承了3个接口
+ AttributeMap。用于保存状态。
+ ChannelInboundInvoker。入站方法。
+ ChannelOutboundInvoker。出站方法。

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
### 4.1 有了Bootstrap，用什么传输都差不多
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
`ByteBuf::readXXX`和`ByteBuf::writeXXX`会推动索引位置。
`ByteBuf::getXXX`和`ByteBuf::setXXX`则不会。
### 5.2 堆


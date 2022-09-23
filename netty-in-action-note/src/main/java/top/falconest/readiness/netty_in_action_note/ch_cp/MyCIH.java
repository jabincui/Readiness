package top.falconest.readiness.netty_in_action_note.ch_cp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

public class MyCIH implements ChannelInboundHandler {
  /**
   * channel注册到loop，且能处理IO时
   */
  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
  }

  /**
   * channel从loop注销，不再能处理IO时
   */
  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
  }

  /**
   * channel已经连接/绑定就绪时
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
  }

  /**
   * channel断开连接时
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
  }

  /**
   * 从channel读到数据时（怎么截取来着？）
   * 需要显式释放池化的ByteBuf，或者直接用{@link io.netty.channel.SimpleChannelInboundHandler}
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ReferenceCountUtil.release(msg);
  }

  /**
   * channel上一个读操作完成时（read的post切片？）需要把可读字节都读完，
   * 因此前面可能已经发生了若干次channelRead
   */
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
  }

  /**
   * 没看懂
   */
  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
  }

  /**
   * channel可写状态改变时。
   * 相关方法：使用{@link Channel#isWritable()}查询可写状态，
   * 使用{@link Channel#config()}.setWriteHighWaterMark()和.setWriteLowWaterMark()
   * 设置可写性阈值
   */
  @Override
  public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
  }

  /**
   * CH添加到CP中
   */
  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
  }

  /**
   * CH从CP中移出
   */
  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
  }

  /**
   * CP的处理过程（CIH或者COH方法）有错误
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
  }
}
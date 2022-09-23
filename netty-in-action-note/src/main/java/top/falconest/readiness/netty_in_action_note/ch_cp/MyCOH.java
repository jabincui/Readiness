package top.falconest.readiness.netty_in_action_note.ch_cp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

public abstract class MyCOH implements ChannelOutboundHandler {
  /**
   * channel绑定到本地地址
   */
  @Override
  public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {}

  /**
   * channel连接到远程节点
   */
  @Override
  public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {}

  /**
   * channel从远程节点断连
   */
  @Override
  public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {}

  /**
   * 关闭channel
   */
  @Override
  public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {}

  /**
   * channel从loop注销
   */
  @Override
  public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {}

  /**
   * channel读取数据
   */
  @Override
  public void read(ChannelHandlerContext ctx) throws Exception {}

  /**
   * channel写数据
   */
  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {}

  /**
   * channel刷数据
   */
  @Override
  public void flush(ChannelHandlerContext ctx) throws Exception {}
}

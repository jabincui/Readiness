package top.falconest.readiness.netty_in_action_note.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import top.falconest.readiness.netty_in_action_note.misc.Configuration;

import java.net.InetSocketAddress;

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

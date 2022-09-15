package top.falconest.readiness.netty_in_action_note.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.falconest.readiness.netty_in_action_note.misc.Configuration;

import java.net.InetSocketAddress;

public class EchoClient {

  public void start() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap boot = new Bootstrap();
      boot.group(group)
          .channel(NioSocketChannel.class)
          .remoteAddress(new InetSocketAddress(Configuration.host, Configuration.port))
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch)
                throws Exception {
              ch.pipeline().addLast(
                  new EchoClientHandler());
            }
          });
      ChannelFuture f = boot.connect().sync();

      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }

  public static void main(String[] args) throws Exception {
    new EchoClient().start();
  }
}

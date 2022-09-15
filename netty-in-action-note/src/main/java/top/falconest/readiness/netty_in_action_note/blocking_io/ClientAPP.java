package top.falconest.readiness.netty_in_action_note.blocking_io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class ClientAPP {
  public void client(String host, int port) throws IOException {
    Socket socket = new Socket(host, port);
    PrintWriter out = new PrintWriter(
        new OutputStreamWriter(socket.getOutputStream()), true
    );
    out.println("write");
    BufferedReader in = new BufferedReader(
        new InputStreamReader(socket.getInputStream())
    );
    System.out.println(in.readLine());
    out.close();
    in.close();
    socket.close();
//    try {
//      TimeUnit.SECONDS.sleep(1);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
  }

  private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

  public void nettyClient() {
    Channel channel = CHANNEL_FROM_SOMEWHERE;
    // Does not block
    ChannelFuture future = channel.connect(
        new InetSocketAddress(BlockingIoServerApp.Configuration.host,
            BlockingIoServerApp.Configuration.port));
    future.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
          ByteBuf buffer = Unpooled.copiedBuffer(
              "Hello\n", Charset.defaultCharset());
          ChannelFuture wf = future.channel()
              .writeAndFlush(buffer);
          // ...
        } else {
          Throwable cause = future.cause();
          cause.printStackTrace();
        }
      }
    });


  }
  public static void run() throws IOException {
    new ClientAPP().client(BlockingIoServerApp.Configuration.host,
        BlockingIoServerApp.Configuration.port);
//    new ClientAPP().nettyClient();
  }

  public static void main(String[] args) throws IOException {
    run();
  }
}

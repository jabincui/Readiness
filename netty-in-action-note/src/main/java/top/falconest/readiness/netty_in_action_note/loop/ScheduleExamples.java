package top.falconest.readiness.netty_in_action_note.loop;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduleExamples {
  private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

  public static void scheduleViaEventLoop() {
    Channel ch = CHANNEL_FROM_SOMEWHERE; // get reference from somewhere
    ScheduledFuture<?> future = ch.eventLoop().schedule(
        () -> System.out.println("60 seconds later"),
        60, TimeUnit.SECONDS);
    future = ch.eventLoop().scheduleAtFixedRate(
        () -> System.out.println("Run every 60 seconds"),
        60, 60, TimeUnit.SECONDS);
    future.cancel(false);
  }
}

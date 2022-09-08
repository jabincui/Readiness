package top.falconest.java_basis.concurrancy.api;

import java.util.concurrent.Executors;

public class ThreadAPI {
  static void threadAPI() {
    new Thread(() -> {
      // Runnable匿名类
      String threadName = Thread.currentThread().getName();
      System.out.println("threadName = " + threadName);
    }).start();

  }
}

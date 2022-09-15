package top.falconest.readiness.java_basis.concurrency.api;

public class ThreadAPI {
  static void threadAPI() {
    new Thread(() -> {
      // Runnable匿名类
      String threadName = Thread.currentThread().getName();
      System.out.println("threadName = " + threadName);
    }).start();

  }
}

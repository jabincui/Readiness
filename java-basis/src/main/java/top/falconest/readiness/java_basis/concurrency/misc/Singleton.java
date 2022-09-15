package top.falconest.readiness.java_basis.concurrency.misc;

import java.util.concurrent.TimeUnit;

/**
 * 双重检查单例模式
 */
public class Singleton {
  private volatile static Singleton singleton;
  private static int instCount = 0;
  public int val = 1;
  private Singleton() {
//    try {
//      TimeUnit.SECONDS.sleep(2);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
  }

  public static Singleton getInstance() {
    // 一重检查
    if (singleton == null) {
      // 如果没有锁，singleton==null时，会有多个线程进去创建实例
      synchronized (Singleton.class) {
        // 二重检查  有锁之后，多个线程有序进来，第二个及以后的线程进来就不会再创建新实例了
        if (singleton == null) {
          singleton = new Singleton();
          instCount++;
        }
      }
    }
    return singleton;
  }

  public static void main(String[] args) {
    for (int i = 0; i < 10000; i++) {
      new Thread(() -> {
        Singleton singleton = Singleton.getInstance();
        singleton.val++;
      }).start();
    }
    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("instCount = " + instCount);
  }
}

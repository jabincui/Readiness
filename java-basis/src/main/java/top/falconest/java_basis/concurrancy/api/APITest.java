package top.falconest.java_basis.concurrancy.api;

import java.util.Arrays;

public class APITest {

  private void threadAPI() {
    // 1. Thread构造方法
    // 1.1 使用lambda表达式创建Runnable匿名内部类，this指向外部类
    new Thread(() -> {
      System.out.println("1.1 class name = " + this.getClass().getName());
    }).start();
    // 1.2 使用一般Runnable匿名内部类，this指向Runnable匿名类
    new Thread(new Runnable() {
      @Override
      public void run() {
        System.out.println("1.2 class name = " + this.getClass().getName());
      }
    }).start();

    // 1.3 指定thread实例的名字
    String threadName = "thread name";
    Thread threadWithName = new Thread(() -> {
      System.out.println("" + Thread.currentThread().getName());
    }, threadName);

    // 2 一些get
    System.out.println("2 id = " + threadWithName.getId());
    System.out.println("2 name = " + threadWithName.getName());
    System.out.println("2 priority = " + threadWithName.getPriority());
    System.out.println("2 thread group = " + threadWithName.getThreadGroup());
    System.out.println("2 context class loader = " + threadWithName.getContextClassLoader());
    System.out.println("2 stack trace = " + Arrays.toString(threadWithName.getStackTrace()));

  }


  public static void main(String[] args) {
    new APITest().threadAPI();
  }
}

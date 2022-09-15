package top.falconest.readiness.java_basis.concurrency.kw_volatile;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对比五种整型声明方式的并发安全
 */
public class Sale {
//  private Ticket ticket;
//  public Sale(int ticketNum) {
//    ticket = new Ticket(ticketNum);
//  }

  private void sale(int sellerNum) {
    Runnable r = new Runnable() {
      int sold = 0; // 一般的变量
      int sSold = 0; // 加锁操作volatile变量
      volatile int vSold = 0; // volatile变量
      volatile int svSold = 0; // 加锁操作volatile变量
      AtomicInteger atomSold = new AtomicInteger(0);
      @Override
      public void run() {
        if (!Thread.currentThread().getName().equals("check var")) {
          for (int i = 0; i < 1000; i++) {
            sold++;
            vSold++;
            synchronized (this) {
              sSold++;
              svSold++;
            }
            atomSold.getAndIncrement();
          }
        } else {
          try {
            TimeUnit.SECONDS.sleep(5);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          System.out.println("sold = " + sold);
          System.out.println("sSold = " + sSold);
          System.out.println("vSold = " + vSold);
          System.out.println("svSold = " + svSold);
          System.out.println("atomSold = " + atomSold.get());
        }
      }
    };

    for (int i = 0; i < sellerNum; i++) {
      new Thread(r).start();
    }
    new Thread(r, "check var").start();

  }
  public static void main(String[] args) {
    new Sale().sale(1000);
  }
}

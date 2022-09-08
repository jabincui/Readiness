package top.falconest.java_basis.concurrancy.ticket_sale;


import top.falconest.java_basis.concurrancy.misc.AtomicTicket;
import top.falconest.java_basis.concurrancy.misc.Ticket;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Sale {
  private Ticket ticket;
  private AtomicTicket atomicTicket;
  Map<Integer, Integer> sellScores = new ConcurrentHashMap<>();
//  Map<Integer, Integer> sellScores = new HashMap<>();
//  volatile int complete = 0;

  abstract static class SellerInf implements Runnable {
    protected final int ind;
    public SellerInf(int ind) { this.ind = ind; }
  }

  class Seller extends SellerInf {
    public Seller(int ind) {
      super(ind);
    }
    @Override
    public void run() {
      while (ticket != null && ticket.sell()) {
        sellScores.put(ind, sellScores.getOrDefault(ind, 0) + 1);
      }
    }
  }

  class SmartSeller extends SellerInf {
    public SmartSeller(int ind) {
      super(ind);
    }
    @Override
    public void run() {
      while (ticket != null && ticket.synSell()) {
        sellScores.put(ind, sellScores.getOrDefault(ind, 0) + 1);
      }
    }
  }

  class AtomicSeller extends SellerInf {
    public AtomicSeller(int ind) {
      super(ind);
    }
    @Override
    public void run() {
      while (atomicTicket != null && atomicTicket.sell()) {
        sellScores.put(ind, sellScores.getOrDefault(ind, 0) + 1);
      }
    }
  }

  public Sale(int totalTickets) {
    this.ticket = new Ticket(totalTickets);
    this.atomicTicket = new AtomicTicket(totalTickets);
  }

  private static void sale(String sellerClassName, int sellers, int totalTickets)
      throws NoSuchMethodException, InvocationTargetException,
      InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {
    Sale sale = new Sale(totalTickets);
    Thread[] threads = new Thread[sellers];
    for (int i = 0; i < sellers; ++i) {
      // 构造非静态内部类需要外部类实例
      Object seller = Class.forName(sellerClassName)
          .getDeclaredConstructor(Sale.class, int.class).newInstance(sale, i);
      if (seller instanceof Runnable){
        threads[i] = new Thread((Runnable) seller);
        threads[i].start();
      }
    }
    for (int i = 0; i < sellers; ++i) {
      threads[i].join();
    }
    System.out.println("sellers size = " + sale.sellScores.keySet().size());
    System.out.println("sold tickets sum = " +
        sale.sellScores.values().stream().mapToInt(Integer::intValue).sum());
  }

  /**
   * Test A. 运行{@link Sale#sale(String, int, int)}方法，其中第一个参数是{@link Seller}的class name
   */
  public static void main(String[] args)
      throws InterruptedException, ClassNotFoundException,
      InvocationTargetException, NoSuchMethodException,
      InstantiationException, IllegalAccessException {
    sale(Seller.class.getName(), 100, 10000);
//    sale(SmartSeller.class.getName(), 100, 10000);
//    sale(AtomicSeller.class.getName(), 100, 10000);
  }
}

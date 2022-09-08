package top.falconest.java_basis.concurrancy.future;

import top.falconest.java_basis.concurrancy.misc.AtomicTicket;
import top.falconest.java_basis.concurrancy.misc.Sleep;
import top.falconest.java_basis.concurrancy.misc.Ticket;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableSale {

  private AtomicTicket atomicTicket;
  private int complete = 0;
  private Map<String, Integer> sellScores = new ConcurrentHashMap<>();

  private synchronized void complete() {
    complete++;
  }

  private void sale(int sellers, int totalTickets) {
    ExecutorService threadPool = Executors.newFixedThreadPool(sellers);
    atomicTicket = new AtomicTicket(totalTickets);
    for (int i = 0; i < sellers; i++) {
      CompletableFuture.runAsync(() -> {
        String name = Thread.currentThread().getName();
        int sellScore = 0;
        while (atomicTicket.sell()) {
          sellScore++;
          Sleep.sleep(100);
        }
        sellScores.put(name, sellScore);
        complete();
        if (complete == sellers) {
          System.out.println(sellScores.values());
          int sum = sellScores.values().stream().mapToInt(v -> v).sum();
          System.out.println("sum = " + sum);
        }
      }, threadPool);
    }

    threadPool.shutdown();
  }

  public static void main(String[] args) {
    new CompletableSale().sale(100, 10000);
  }
}

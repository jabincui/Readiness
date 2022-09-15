package top.falconest.readiness.java_basis.concurrency.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class NetMall {
  @Getter
  private String netMallName;

  public double calcPrice(String productName) {
    try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }

    return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
  }
}

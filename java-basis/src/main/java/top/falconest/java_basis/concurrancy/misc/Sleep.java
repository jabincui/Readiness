package top.falconest.java_basis.concurrancy.misc;

import java.util.concurrent.TimeUnit;

public class Sleep {
  public static void sleep() {
    sleep(1000);
  }

  public static void sleep(int n) {
    try {
      TimeUnit.MILLISECONDS.sleep(n);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

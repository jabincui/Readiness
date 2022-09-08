package top.falconest.java_basis.concurrancy.misc;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicTicket {
  private AtomicInteger total = new AtomicInteger(30);

  public AtomicTicket() {}

  public AtomicTicket(int total) {
    this.total.set(total);
  }

  /**
   * 自旋式原子操作售票
   * @return 是否售票成功
   */
  public boolean sell() {
    while (true) {
      int cas = total.get();
      if (cas > 0) {
//      log.info("sell: last tickets num = " + total);
//      System.out.println("sell: last tickets num = " + total);
        if (total.compareAndSet(cas, cas-1)) {
          return true;
        }
      } else {
        return false;
      }
    }
  }

}

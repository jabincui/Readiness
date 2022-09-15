package top.falconest.readiness.java_basis.concurrency.misc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ticket {
  private int totalNum = 30;

//  public Ticket() {}

  public Ticket(int totalNum) {
    this.totalNum = totalNum;
  }

  /**
   * 售票
   * @return 是否售票成功
   */
  public boolean sell() {
    if (totalNum > 0) {
      totalNum--;
      log.info("sell: last tickets num = " + totalNum);
//      System.out.println("sell: last tickets num = " + total);
      return true;
    } else {
      return false;
    }
  }

  /**
   * {@link #sell()}的同步方法
   */
  public synchronized boolean synSell() {
    return sell();
  }
}

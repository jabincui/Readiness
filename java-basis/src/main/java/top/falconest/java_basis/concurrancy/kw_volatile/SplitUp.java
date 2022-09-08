package top.falconest.java_basis.concurrancy.kw_volatile;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 分头行动
 */
@Slf4j
public class SplitUp {
  int a = 0;
  void run() {
    new Thread(() -> {
      try {
        TimeUnit.SECONDS.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      a = 2;
    }).start();

    new Thread(() -> {
      while (true) {
        System.out.println(a);
      }
    }).start();
  }

  public static void main(String[] args) {
    new SplitUp().run();
  }
}

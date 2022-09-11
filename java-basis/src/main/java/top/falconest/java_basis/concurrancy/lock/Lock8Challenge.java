package top.falconest.java_basis.concurrancy.lock;

import top.falconest.java_basis.concurrancy.misc.Phone;
import top.falconest.java_basis.concurrancy.misc.Sleep;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 全文主旨：
 *    成员方法上对象锁
 *    静态方法上类锁
 */
public class Lock8Challenge {

  static final Phone phone = new Phone();
  static final Phone sparePhone = new Phone();
  static final ExecutorService threadPool = Executors.newFixedThreadPool(2);

  static void challenge1() {
    CompletableFuture.runAsync(phone::sendEmail, threadPool);
    Sleep.sleep(100);
    CompletableFuture.runAsync(phone::sendSMS, threadPool);
  }

  /**
   * [sout] <br>
   * synchronized send email (delay) <br>
   * synchronized send sms <br>
   *
   * 成员方法锁住的是this，{@link Thread#sleep(long)} 抱着锁睡觉
   */
  static void challenge2() {
    CompletableFuture.runAsync(() -> phone.sendEmailDelay(200), threadPool);
    Sleep.sleep(100);
    CompletableFuture.runAsync(phone::sendSMS, threadPool);
  }

  /**
   * [sout] <br>
   * hello <br>
   * synchronized send email (delay) <br>
   *
   * 非同步方法不用等锁
   */
  static void challenge3() {
    CompletableFuture.runAsync(() -> phone.sendEmailDelay(200), threadPool);
    Sleep.sleep(100);
    CompletableFuture.runAsync(phone::hello, threadPool);
  }

  /**
   * [sout]
   * synchronized send sms
   * synchronized send email (delay)
   *
   * 证明成员方法锁的是对象，而不是类
   */
  static void challenge4() {
    CompletableFuture.runAsync(() -> phone.sendEmailDelay(200), threadPool);
    Sleep.sleep(100);
    CompletableFuture.runAsync(sparePhone::sendSMS, threadPool);
  }

  /**
   * [sout]
   * static synchronized send email (delay)
   * static synchronized send sms
   *
   * 静态方法锁类
   */
  static void challenge5() {
    CompletableFuture.runAsync(() -> Phone.sendEmailStaticDelay(200), threadPool);
    Sleep.sleep(100);
    CompletableFuture.runAsync(Phone::sendSMSStatic, threadPool);
  }

  /**
   * [sout]
   * static synchronized send email (delay)
   * static synchronized send sms
   */
  static void challenge6() {
    CompletableFuture.runAsync(() -> phone.sendEmailStaticDelay(200), threadPool);
    Sleep.sleep(100);
    CompletableFuture.runAsync(() -> sparePhone.sendSMSStatic(), threadPool);
  }

  /**
   * [sout]
   * static synchronized send email (delay)
   * synchronized send sms
   */
  static void challenge7() {
    CompletableFuture.runAsync(() -> Phone.sendEmailStaticDelay(200), threadPool);
    Sleep.sleep(100);
    CompletableFuture.runAsync(phone::sendSMS, threadPool);
  }

  /**
   * [sout]v
   * static synchronized send email (delay)
   * synchronized send sms
   */
  static void challenge8() {
    CompletableFuture.runAsync(() -> phone.sendEmailStaticDelay(200), threadPool);
    Sleep.sleep(100);
    CompletableFuture.runAsync(sparePhone::sendSMS, threadPool);
  }

  public static void main(String[] args) {
    challenge8();
    threadPool.shutdown();
  }

}

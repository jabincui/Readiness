package top.falconest.readiness.java_basis.concurrency.misc;

public class Phone {
  public synchronized void sendEmail() {
    System.out.println("synchronized send email");
  }

  public synchronized void sendEmailDelay(int ms) {
    Sleep.sleep(ms);
    System.out.println("synchronized send email (delay)");
  }

  public static synchronized void sendEmailStaticDelay(int ms) {
    System.out.println("static synchronized send email (delay)");
  }


  public synchronized void sendSMS() {
    System.out.println("synchronized send sms");
  }

  public static synchronized void sendSMSStatic() {
    System.out.println("static synchronized send sms");
  }

  public void hello() {
    System.out.println("hello");
  }

}

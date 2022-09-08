package top.falconest.java_basis.concurrancy.misc;

@FunctionalInterface
public interface Monitor {

  default void run() {
    long start = System.currentTimeMillis();
    callback();
    long end = System.currentTimeMillis();
    System.out.println("cost time = " + (end-start) + "ms");
  }

  void callback();

}

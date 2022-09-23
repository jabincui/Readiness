package top.falconest.readiness.java_basis.redundant;

/**
 * 用==比较两个数值相等的int或Integer
 * <ul>
 *   <li>只要有一个int，就是true（即使这个int不在缓冲池）</li>
 *   <li>两个都是Integer，取自（自动装箱的）缓冲池int，就是true</li>
 *   <li>其他情况都是false</li>
 * <ul/>
 */
public class IntegerEquals {
  static void equals1() {
    Integer a = new Integer(1);
    Integer b = new Integer(1);
    System.out.println(a.equals(b)); // always true
    System.out.println(a == b); // false
  }

  static void equals2() {
    Integer a = 1;
    Integer b = Integer.valueOf(1);
    System.out.println(a == b); // true
  }

  static void equals3() {
    Integer a = new Integer(1000);
//    Integer a = Integer.valueOf(1);
    int b = Integer.valueOf(1000);
    System.out.println(a == b); // true
  }

  static void equals4() {
    Integer a = new Integer(1);
    Integer b = Integer.valueOf(1);
    System.out.println(a == b); // false
  }

  static void equals5() {
    Integer a = Integer.valueOf(128);
    Integer b = Integer.valueOf(128);
    System.out.println(a == b); // false
  }

  static void equals6() {
    Integer a = Integer.valueOf(127);
    Integer b = Integer.valueOf(127);
    System.out.println(a == b); // false
  }

  public static void main(String[] args) {
    equals2();
  }
}

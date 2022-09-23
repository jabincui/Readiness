package top.falconest.readiness.java_basis.stream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * jdk序列化问题
 */
public class SerialQuestion {
  public static void main(String[] args) {
    Forest f = new Forest();
    try {
      FileOutputStream fos = new FileOutputStream("f.out");
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(f);
      oos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class Forest implements Serializable {
  private Tree tree = new Tree();
}

class Tree { } // fatal error : not serializable
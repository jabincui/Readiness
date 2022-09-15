package top.falconest.readiness.java_basis.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * 每行代码警告和错误信息见注释。
 * 前三种写法都能正常读写。
 */
public class CollectionArray {
  public static void main(String[] args) {
    // Unchecked assignment: 'java.util.ArrayList[]' to 'java.util.List<java.lang.Integer>[]'
    List<Integer>[] listArray1 = new ArrayList[10];
    // Raw use of parameterized class 'ArrayList'
    ArrayList[] listArray2 = new ArrayList[10];
    // Raw use of parameterized class 'List'
    List[] listArray3 = new ArrayList[10];
    // Cannot infer arguments
//    List<Integer>[] listArray4 = new ArrayList<>[10];
//    // Generic array creation
//    List<Integer>[] listArray5 = new ArrayList<Integer>[10];

    listArray1[0] = new ArrayList<>();
    listArray1[0].add(1);
    System.out.println(listArray1[0].get(0)); // 打印1，没有bug

    listArray2[0] = new ArrayList<>();
    // Unchecked call to 'add(E)' as a member of raw type 'java.util.ArrayList'
    listArray2[0].add(1);
    System.out.println(listArray2[0].get(0)); // 打印1，没有bug

    listArray3[0] = new ArrayList<>();
    // Unchecked call to 'add(E)' as a member of raw type 'java.util.List'
    listArray3[0].add(1);
    System.out.println(listArray3[0].get(0)); // 打印1，没有bug

  }
}

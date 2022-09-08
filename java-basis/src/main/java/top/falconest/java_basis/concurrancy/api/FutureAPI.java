package top.falconest.java_basis.concurrancy.api;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class FutureAPI {
  static void futureAPI() {
    // 构造注入Callable，有返回值
    Future<Integer> futureWithRet = new FutureTask<>(new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        return 1;
      }
    });
    
    // 构造注入Runnable
    Integer a = null;
    Future<Integer> futureWithoutRet = new FutureTask<>(new Runnable(){
      @Override
      public void run() {

      }
    }, a);

  }
}

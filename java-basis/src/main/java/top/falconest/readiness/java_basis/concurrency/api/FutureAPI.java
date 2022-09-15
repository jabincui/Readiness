package top.falconest.readiness.java_basis.concurrency.api;

import top.falconest.readiness.java_basis.concurrency.misc.Sleep;

import java.util.concurrent.*;

public class FutureAPI {
  static void futureAPI() throws ExecutionException, InterruptedException, TimeoutException {
    // 构造注入Callable，有返回值
    // 不能声明为Future类型，因为没继承Runnable
    FutureTask<Integer> futureWithRet = new FutureTask<>(new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        return 1;
      }
    });

//    new Thread(FutureAPI::futureAPI).start(); // ?
    new Thread(futureWithRet).start();
    futureWithRet.isDone(); // 询问是否完成
    futureWithRet.get();// 阻塞，应该往后放
    futureWithRet.get(2, TimeUnit.SECONDS);// 阻塞固定时间，超时就算了

    // 构造注入Runnable
    Integer a = null;
    Future<Integer> futureWithoutRet = new FutureTask<>(new Runnable(){
      @Override
      public void run() {

      }
    }, a);
  }

  static void exePoolAPI() {
    ExecutorService threadPool = Executors.newFixedThreadPool(3);
    FutureTask<Integer> task1 = new FutureTask<>(() -> {
      TimeUnit.SECONDS.sleep(1);
      return 1;
    });
    threadPool.submit(task1);
    FutureTask<Integer> task2 = new FutureTask<>(() -> {
      TimeUnit.SECONDS.sleep(2);
      return 1;
    });
    threadPool.submit(task2);
    FutureTask<Integer> task3 = new FutureTask<>(() -> {
      TimeUnit.SECONDS.sleep(1);
      return 1;
    });
    threadPool.submit(task3);

    threadPool.shutdown();
  }

  static void completableFutureAPI() throws ExecutionException, InterruptedException {
    // 线程池
    ExecutorService threadPool = Executors.newFixedThreadPool(3);
    // CompletableFuture的两种创建方法：注入Supplier或Runnable
    // 完事之后直接跑起来了
    CompletableFuture<Integer> completableFuture =
        CompletableFuture
            .supplyAsync(() -> {
              System.out.println(Thread.currentThread().getName());
              Sleep.sleep();
              return 1;
              }, threadPool)
            .whenComplete((v, e) -> {
              // 用自己定义的线程池可以等到whenComplete，否则可能等不到。
              if (e == null)
                System.out.println(v);
            }).exceptionally(e -> {
              e.printStackTrace();
              return null;
            });

    // get和join的区别
    // get 报检查异常 必须处置 包括：除了RuntimeException和Error
    // join 不抛异常
    Integer get = completableFuture.get();
    Integer join = completableFuture.join();

    int defaultIntValue = 0;
    // 没完成的话就返回defaultIntValue
    completableFuture.getNow(defaultIntValue);
    // 没完成的话就不继续做，再get就是defaultIntValue
    completableFuture.complete(defaultIntValue);

    CompletableFuture<Void> completableFutureWithoutRet =
        CompletableFuture.runAsync(() -> {
          // Runnable匿名类
        }, threadPool);

    threadPool.shutdown();
  }

  static void completableFutureAPI2() {
    // 进一步处理(有返回值)：thenApply 和 handle
    CompletableFuture
        .supplyAsync(() -> 1)
        .thenApply(f -> f+1)
        // 和thenApply差不多，但是捕获异常
        .handle((v, e) -> {
          if (e == null) {
            return v+1;
          } else {
            e.printStackTrace();
          }
          // 未验证对后续的影响
          return null;
        })
        .whenComplete((v, e) -> {})
        .exceptionally(e -> null);

    // 中间消费（无返回值）
    CompletableFuture
        .supplyAsync(() -> 1)
        .thenAccept(System.out::println)
        // 和thenAccept类似，区别是不需要上一步结果
        .thenRun(() -> {});
  }


  static void completableStageAPI() {

  }


}

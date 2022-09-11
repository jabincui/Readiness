package top.falconest.java_basis.concurrancy.api;

import top.falconest.java_basis.concurrancy.misc.Sleep;

import java.util.concurrent.CompletableFuture;

public class FutureAPI2 {
  static void completableFutureAPI() {
    // 挑出快的那个
    CompletableFuture<Integer> parallelA =
        CompletableFuture.supplyAsync(() -> {
          Sleep.sleep(100);
          return 1;
        });
    CompletableFuture<Integer> parallelB = CompletableFuture
        .supplyAsync(() -> {
          Sleep.sleep(200);
          return 2;
        });
    parallelA.applyToEither(parallelB, f -> f);

    CompletableFuture<Integer> submissionA = CompletableFuture
        .supplyAsync(() -> {
          Sleep.sleep();
          return 1;
        });

    CompletableFuture<Integer> submissionB = CompletableFuture
        .supplyAsync(() -> {
          Sleep.sleep();
          return 2;
        })
        .thenCombine(submissionA, (v1, v2) -> v1*v2);

  }
}

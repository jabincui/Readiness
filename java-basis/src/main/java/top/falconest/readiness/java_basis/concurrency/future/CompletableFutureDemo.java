package top.falconest.readiness.java_basis.concurrency.future;

import top.falconest.readiness.java_basis.concurrency.misc.NetMall;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CompletableFutureDemo {
  static List<NetMall> list = Arrays.asList(
      new NetMall("jd"),
      new NetMall("dangdang"),
      new NetMall("taobao"),
      new NetMall("pdd"),
      new NetMall("tmall")
  );

  static List<String> getMinPrice(List<NetMall> list, String productName) {
    return list.stream()
        .map(netMall -> String.format(productName + " in %s price is %.2f",
            netMall.getNetMallName(),
            netMall.calcPrice(productName)))
        .collect(Collectors.toList());
  }

  static List<String> getPriceByCompletableFuture(List<NetMall> list,String productName)
  {
    return list.stream()
        // NetMall -> CompletableFuture
        .map(netMall ->
            CompletableFuture.supplyAsync(() -> String.format(productName + " in %s price is %.2f",
                netMall.getNetMallName(),
                netMall.calcPrice(productName))))
        .collect(Collectors.toList())
        .stream()
        // CompletableFuture -> String
        .map(s -> s.join())
        .collect(Collectors.toList());
  }


}

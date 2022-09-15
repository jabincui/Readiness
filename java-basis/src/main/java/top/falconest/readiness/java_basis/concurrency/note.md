## 引言
此部分，即包top.falconest.readiness.java_basis.concurrency，介绍了并发编程的基础问题，API和用例。
参考资料：尚硅谷JUC
# 并发编程
名词解释 \
并发：多个任务在公共资源上执行（其中，任务在这里指线程或进程，下同），例如
+ 单核CPU同时执行多个任务
+ 
并行：
## 1. 多线程
### 1.1 多线程API
Thread API
FutureTask API
CompletableFuture API

## 2. 线程安全初探
### 2.1 synchronized
### 2.2 一个线程不安全的例子
a) 案例介绍：多线程售票，卖完结束，统计每个线程售出票的数量，以判断是否存在线程安全问题 \
b) 执行代码：code -> 1.1 \
c) 案例分析：

### 1.3 线程安全实现一：synchronized

## 3. 锁
### 乐观锁，悲观锁
乐观锁两种实现方式：// 没搞明白
+ 版本号：读数据和版本号 -> 处理数据 -> 判断版本号没变则更新数据，版本号+1 -> 否则重新处理
+ cas
### 8锁（8种锁的情况）


monitorenter // 进锁的字节码
monitorexit  // 出锁的字节码（为了避免异常导致跳过这个字节码，这个字节码会出现2次）

ACC_SYNCHRONIZED // 
ACC_STATIC // 

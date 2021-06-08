package com.eugene.basic.futurepattern.jdk;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 使用completable模拟计算某个结果
 *
 */
public class CompletableFutureTest {

    public static Integer calc(int x) {
        try {
            Thread.sleep(2000);
            // 模拟计算耗时2分钟
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return x * x;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {

        /**
         * 在此处构造了一个CompletableFuture实例，在supplyAsync函数中，它会在一个新的线程中执行传入的参数（此时的参数是一个函数）
         * 在这里，它会执行calc方法。而calc方法执行的比较慢，但它并不会影响completableFuture的返回，
         * 因此supplyAsync是立即返回的。
         * 这样我们可以利用返回的completableFuture对象在“合适”的时候获取对应的结果，
         *
         * 这里有个注意事项：
         * 1、supplyAsync支持传入一个自定义的线程池，如果不指定则使用系统默认的线程池（ForkJoinPool.commonPool()方法）,
         * 但这个线程池内部的所有线程都是守护线程（daemon为true）。这意味着如果主线程退出，completableFuture内部的线程
         * 无论是否执行完都会退出系统
         * 2、exceptionally为completableFuture优雅处理异常的方案，它会提供原异常，同时还需要返回一个默认值。
         * 这个默认值的类型与最近的一次操作相关，比如在这个例子中，exceptionally最近的上一次操作中的返回值为integer，
         * 因为这个exception只能catch住上一个thenApply操作。同时要注意上述的第一点，有可能daemon线程执行到要抛异常的
         * 地方了，此时主线程结束了，那么这个异常也不会被抛出来，因为会直接结束。
         *
         */
        CompletableFuture<Integer> calc = CompletableFuture.supplyAsync(() -> calc(50));
        calc.thenApply(result -> result + 1)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return 0;
                })
                .thenApply(result -> {
                    Object t = null;
                    t.toString(); return result.toString();})
                .thenApply(result -> result += "avengerEug")
                .thenAccept(System.out::print);

        Integer integer = calc.get();

        // 这里挂起主线程，为了让completableFuture里的线程执行完所有的逻辑
        System.in.read();
    }


}

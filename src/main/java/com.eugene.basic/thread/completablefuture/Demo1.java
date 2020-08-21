package com.eugene.basic.thread.completablefuture;

import java.util.concurrent.*;

/**
 * 测试CompletableFuture
 */
public class Demo1 {


    /**
     * 自定义线程池
     */
    public static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            2,
            5,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        testCompletableFuturePromise();

        threadPool.shutdown();
    }

    /**
     * 测试completableFuture类似于前端js promise的功能, then catch的方式。
     *
     * 1、whenComplete类似于与js promise的then，会等待上游的运行结果结束，可以接收上游执行的返回值和异常
     *   第一个参数为上游执行的结果(返回值)
     *   第二个参数为发生异常的信息
     *
     *   如果有异常，第二个参数存储的是导致异常的原因。如果无异常，第二个参数存储的值为null
     * 2、exceptionally，
     *   抛异常时，才会被回调，同时还能返回默认返回值，修改上游程序执行的结果
     *
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void testCompletableFuturePromise() throws InterruptedException, ExecutionException {

        CompletableFuture<Integer> handle = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "执行方法");
            return 10 / 0;
        }, threadPool).whenComplete((result, exception) -> {
            System.out.println("result: " + result);
            System.out.println("异常：" + exception + ", whenComplete可以接收到异常，但是无返回值");
        }).exceptionally(exception -> {
            System.out.println("异常原因：" + exception);
            System.out.println("exceptionally可以接收异常，也可以将值进行返回（可以返回默认值）");
            // 返回默认值
            return 1;
        });

        System.out.println("主线程运行结束，任务返回值：" + handle.get());
    }
}

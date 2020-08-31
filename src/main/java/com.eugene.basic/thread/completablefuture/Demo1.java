package com.eugene.basic.thread.completablefuture;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 测试CompletableFuture
 *
 * CompletableFuture提供了四个静态方法来创建CompletableFuture
 * 分别是：
 * 分为两大类：
 * runAsync和supplyAsync
 * runAsync是异步执行的，但是没有返回值，也就是说我们无法获取到任务的返回结果
 * supplyAsync也是异步执行的，但是我们可以拿到它的返回值。
 * ps: 所有的返回结果最终都是要根据runAsync或者supplyAsync静态方法返回的CompletableFuture的get方法来获取
 *
 *
 *
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

    public static void main(String[] args) throws Exception {

        //testCompletableFuturePromise();

        //testCompletableFutureHandler();

        testCompletableFutureThenApply();
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


    /**
     * 测试completable中的handler方法
     *
     * completable的handler方法与whenComplete方法不同的是，它能感知到异常，并且还能对结果进行修改
     *
     * handle中的执行规则：
     * 1、未抛出异常，result不为null，exception为null
     * 2、抛出异常，result为null，exception不为null，此时的exception为发生异常的信息(即将exception.getMessage()的值传到了第二个参数中)
     *
     *
     * @throws Exception
     */
    private static void testCompletableFutureHandler() throws Exception {
        CompletableFuture<Integer> handle = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "执行方法");
            return 10 / 0;
        }, threadPool).handle((result, exception) -> {
            System.out.println("result = " + result);
            System.out.println("异常：" + exception);
            return 10;
        });

        System.out.println("运行结果：" + handle.get());
    }

    /**
     * 测试completableFuture中的thenAcceptAsync和thenApplyAsync方法(线程串行化)
     *
     * thenAcceptAsync方法可以获取到结果，但是不能对返回值做二次修改
     * thenApplyAsync方法可以获取到结果，并且可以对返回值做二次修改
     *
     * @throws Exception
     */
    private static void testCompletableFutureThenApply() throws Exception {
        CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
            Map<String, Object> map = new ConcurrentHashMap<>();
            map.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            return map;
        }, threadPool).thenApplyAsync((result) -> {
            result.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            return result;
        }, threadPool);

        Map<String, Object> map = future.get();

        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            System.out.println("key = " + next + " value = " + map.get(next));
        }

    }

}

package com.eugene.basic.threadpool;

import java.util.concurrent.*;

/**
 * 创建线程的第三种方法：
 * 使用FutureTask和Callable配套使用，
 * 为什么要配套使用呢？因为Callable接口内部只是维护了一个方法，
 * 我们可以把它理解成生产者，
 * 而FutureTask其实也是实现了Runnable接口，所以它可以作为参数
 * 传入Thread中。因为我们使用FutureTask作为线程时，一般是要同步
 * 调用，即获取到它的返回结果，所以一般要使用它的get方法来阻塞等待
 * 返回值。
 */
public class CallableTest {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Eugene from thread";
            }
        });

        // 使用线程的方式启动futureTask
        new Thread(futureTask).start();
        String val = futureTask.get();
        System.out.println(val);

        // 使用线程池的方式启动futureTask, 我的cpu为6核12线程的，所以设置线程池大小为6 + 1 = 7
        ExecutorService executorService = Executors.newFixedThreadPool(7);
        Future<String> submit = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Eugene from thread pool";
            }
        });

        // 调用get方法，让主线程阻塞，保证拿到返回值后再继续执行
        System.out.println(submit.get());

        System.out.println(executorService);
        System.out.println(executorService.isShutdown());
        System.out.println(executorService.isTerminated());

        // 调用线程池的shutdown方法，让线程池停止工作，
        // 若不调用的话，此程序永远不会结束，因为线程池会在后台一直运行
        executorService.shutdown();

        System.out.println("****shutdown****");
        System.out.println(executorService);
        System.out.println(executorService.isShutdown());
        System.out.println(executorService.isTerminated());

    }
}

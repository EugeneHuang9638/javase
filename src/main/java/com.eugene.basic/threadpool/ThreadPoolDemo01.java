package com.eugene.basic.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executors: 工厂类 -> 用于创建各种线程池
 *
 * eg:
 *  newFixedThreadPool()  =>  创建固定大小的线程池,
 *  newCachedThreadPool()  =>  创建可缓存的线程池, 可变大小的线程池
 *  newSingleThreadExecutor()  =>  线程中只有一个线程的线程池。  将单个线程放入线程池中, 以达到线程的复用性, 减少操作系统操作线程的开销
 *  newScheduledThreadPool()  =>  创建一种延迟执行或者定期执行的线程池,
 *
 *
 */
public class ThreadPoolDemo01 {

    public static void main(String[] args) {
        // 线程池数量大小一般是cpu核心数加1
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            // 将任务都交给线程池中处理
            executorService.execute(() -> {
                System.out.println(Thread.currentThread().getName());
            });
        }

        System.out.println(executorService);

        // 关闭线程任务, 等任务执行完后再关闭
        executorService.shutdown();

        // 将线程池立马关掉
        // executorService.shutdownNow();

        System.out.println(executorService.isShutdown());
        System.out.println(executorService.isTerminated());
        System.out.println(executorService);

    }

}

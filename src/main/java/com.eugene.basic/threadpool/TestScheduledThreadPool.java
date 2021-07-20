package com.eugene.basic.threadpool;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 测试jdk自带的定时任务线程池
 */
public class TestScheduledThreadPool {

    public static void main(String[] args) throws IOException {

//        ExecutorService executorService = Executors.newScheduledThreadPool(5);
//
//        // 5个线程，1s后，每隔2s执行一次
//        ((ScheduledExecutorService) executorService).scheduleAtFixedRate(
//                () -> {
//                    System.out.println(Thread.currentThread().getName());
//                }, 1, 2, TimeUnit.SECONDS);


        ScheduledExecutorService delayExportExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });

        delayExportExecutor.schedule(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("执行了");
                System.out.println(2 / i);
            }
        }, 1, TimeUnit.SECONDS);

        System.in.read();
    }
}

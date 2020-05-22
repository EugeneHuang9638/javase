package com.eugene.basic.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 测试jdk自带的定时任务线程池
 */
public class TestScheduledThreadPool {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newScheduledThreadPool(5);

        // 5个线程，1s后，每隔2s执行一次
        ((ScheduledExecutorService) executorService).scheduleAtFixedRate(
                () -> {
                    System.out.println(Thread.currentThread().getName());
                }, 1, 2, TimeUnit.SECONDS);

    }
}

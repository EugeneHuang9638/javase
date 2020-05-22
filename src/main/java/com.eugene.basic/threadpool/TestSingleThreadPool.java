package com.eugene.basic.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试每次只有存储一个线程的单个线程池
 *
 *
 * 有点鸡肋。。。。
 *
 */
public class TestSingleThreadPool {


    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();


        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                System.out.println(Thread.currentThread().getName());
            });
        }
    }
}

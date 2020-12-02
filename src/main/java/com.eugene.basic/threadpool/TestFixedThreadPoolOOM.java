package com.eugene.basic.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 测试FixedThreadPool内存溢出
 */
public class TestFixedThreadPoolOOM {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        // 执行前设置jvm堆内存为10m  -Xms10m -Xmx10m
        while (true) {

            executorService.submit(() -> {
                byte[] bytes = new byte[1024];
                System.out.println("剩余空闲内存：" + Runtime.getRuntime().freeMemory());
                try {
                    System.out.println("模拟线程执行需要2s，2s后1兆的内存才被释放");
                    System.out.println(bytes.length);
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

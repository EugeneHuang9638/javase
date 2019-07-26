package com.eugene.basic.concurrentpackage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 测试并发包中信号量semaphore api
 *
 */
public class Concurrent3 {

    public static class SemapDemo implements Runnable {

        // 指定信号量的准入数为5
        final Semaphore semaphore = new Semaphore(5);

        @Override
        public void run() {
            try {
                // 申请获得一个许可, 若许可被其他线程申请完了, 则当前线程阻塞
                semaphore.acquire();

                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getId() + ": Done!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 释放许可
                semaphore.release();
            }
        }

        public static void main(String[] args) {
            // 创建20个线程的线程池
            ExecutorService exec = Executors.newFixedThreadPool(20);
            final SemapDemo demo = new SemapDemo();
            for (int i = 0; i < 20; i++) {
                exec.submit(demo);
            }
        }
    }
}

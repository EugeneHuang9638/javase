package com.eugene.basic.concurrentpackage;

import java.util.concurrent.*;

/**
 * 测试CountdownLatch(倒记数器): 主要功能为, 可以控制线程等待,可以让某一个线程等待直到倒计数结束再开始执行
 *
 * 模拟火箭发射任务, 在点火之前, 需要前面的线程(eg: 检查安全，检查系统等等)都执行完毕
 */
public class Concurrent5 {

    public static class CountDownLatchDemo implements Runnable {

        private static final CountDownLatch end = new CountDownLatch(10);
        private static final CountDownLatchDemo demo = new CountDownLatchDemo();
        private static final Semaphore semap = new Semaphore(4);

        @Override
        public void run() {

            try {
                // 休眠一段时间, 模拟线程在做自己的事
                semap.acquire();
                Thread.sleep(1000);
                System.out.println("check completed");
                end.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semap.release();
            }
        }

        public static void main(String[] args) throws InterruptedException {
            ExecutorService exec = Executors.newFixedThreadPool(10);
            for (int i = 0; i < 10; i++) {
                exec.submit(demo);
            }

            // 发生火箭等待其他线程检查工作完毕
            end.await();

            // 发射火箭, 执行主线程
            System.out.println("点火, Fire!");
            exec.shutdown();
        }
    }
}

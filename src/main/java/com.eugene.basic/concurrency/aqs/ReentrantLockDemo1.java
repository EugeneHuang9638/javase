package com.eugene.basic.concurrency.aqs;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo1 {

    // 公平锁
    static ReentrantLock lock = new ReentrantLock(true);

    static Random random = new Random();

    static CountDownLatch latch = new CountDownLatch(5);

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    lock.lock();
                    // 随机睡眠1-5s
                    int sleepTime = random.nextInt(5);
                    TimeUnit.SECONDS.sleep(sleepTime);
                    System.out.println(Thread.currentThread().getName() + "睡眠了 " + sleepTime + "s");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                    latch.countDown();
                }
            }).start();
        }

        // 等待上面的所有线程都执行完
        latch.await();
        System.out.println("main end");
    }
}

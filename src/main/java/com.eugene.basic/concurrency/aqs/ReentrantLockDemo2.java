package com.eugene.basic.concurrency.aqs;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 断点调试ReentrantLock
 *
 * t1拿到锁后一直在睡眠
 * 此时t2调用了lock方法
 * 验证了aqs队列的初始化，以及第二个节点在acquireQueued中自旋一次后，进入park状态
 *
 */
public class ReentrantLockDemo2 {

    // 公平锁
    static ReentrantLock lock = new ReentrantLock(true);

    static Random random = new Random();

    static CountDownLatch latch = new CountDownLatch(5);

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            try {
                lock.lock();
                // 随机睡眠1-5s
                TimeUnit.SECONDS.sleep(10000000);
                System.out.println(Thread.currentThread().getName() + "睡眠 " + 10000000 + "s");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                latch.countDown();
            }
        }, "t1");
        t1.start();

        new Thread(() -> {
            try {
                lock.lock();
                // 随机睡眠1-5s
                int sleepTime = random.nextInt(5);
                TimeUnit.SECONDS.sleep(sleepTime);
                System.out.println(Thread.currentThread().getName() + "睡眠 " + sleepTime + "s");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                latch.countDown();
            }
        }, "t2").start();

        // 等待上面的所有线程都执行完
        latch.await();
        System.out.println("main end");
    }
}

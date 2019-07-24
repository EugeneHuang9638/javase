package com.eugene.basic.concurrentpackage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试并发包 api: ReentrantLock
 *   ReentrantLock: 重入锁, 相比synchronized更灵活，同一个线程可以反复获取锁
 *   重入锁实现的三要素:
 *     1. 原子状态: 使用CAS操作来存储当前锁的状态, 判断锁是否被别的线程持有
 *     2. 等待队列: 所有没有请求到锁的线程会进入等待队列进行等待。
 *     3. 阻塞原语park()和unpark(), 用来挂起和恢复线程
 *
 *   对于synchronized(非公平所锁)同步块而言, 一个线程在等待锁, 结果只有两种情况
 *     1. 获取这把锁继续执行
 *     2. 一直等待
 *   而ReentrantLock它可以
 *     1. 中断响应
 *     2. 申请锁限时
 *     3. 公平锁
 *
 * api:
 *   lockInterruptibly:
 *     若ReentrantLock对象调用lockInterruptibly方法时, 在整个过程中(不管是获取到锁还是正在阻塞中)，只要当前线程被添加中断标志
 *     (调用interrupt方法), 那么此时当前线程若在阻塞(要申请某个锁)过程中则会放弃对锁的申请, 若当前线程处于拥有锁的状态, 则会释放锁
 */
public class Concurrent1 {

    /**
     * 初步测试ReentrantLock完成同步操作
     */
    public static class ReenterLock implements Runnable{

        public static Lock lock = new ReentrantLock();
        public static int i = 0;

        @Override
        public void run() {
            for (int j = 0; j < 10000000; j++) {
                // 添加锁  整体替换了synchronized 的使用
                lock.lock();

                try {
                    i++;
                } finally {
                    // 释放锁
                    lock.unlock();
                }
            }
        }

        public static void main(String[] args) throws InterruptedException {
            ReenterLock r1 = new ReenterLock();
            Thread t1 = new Thread(r1);
            Thread t2 = new Thread(r1);

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            System.out.println(i);
        }

    }


    /**
     * 1. 测试ReentrantLock解决死锁问题（中断响应）,
     *   在下列代码中t1, t2很容易造成死锁情况, 比如这种情形: t1拿着lock1的锁, t2拿着lock2的锁, 同时t1因拿lock2的锁被阻塞, t2因拿lock1的锁
     *   阻塞造成的死锁。 因为t1和t2都是通过lockInterruptibly获取锁, 所以只需要将一个线程添加中断标志即可解决这种情况下的死锁情况,
     *   添加中断标志的线程最终会释放已经拥有的锁，退出正在争夺的锁的队列，停止后续的线程任务。
     */
    public static class IntLock implements Runnable {
        public static ReentrantLock lock1 = new ReentrantLock();
        public static ReentrantLock lock2 = new ReentrantLock();

        int lock;

        public IntLock(int lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                if (lock == 1) {
                    lock1.lockInterruptibly();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    lock2.lockInterruptibly();

                    System.out.println("t1 剩余的任务");
                } else {
                    lock2.lockInterruptibly();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lock1.lockInterruptibly();


                    System.out.println("t2 剩余的任务");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 查询当前线程是否拥有lock1的锁定
                if (lock1.isHeldByCurrentThread()) {
                    lock1.unlock();
                }

                // 查询当前线程是否拥有lock2的锁定
                if (lock2.isHeldByCurrentThread()) {
                    lock2.unlock();
                }

                System.out.println(Thread.currentThread().getId() + ": 线程退出");
            }
        }

        public static void main(String[] args) throws InterruptedException {
            IntLock r1 = new IntLock(1);
            IntLock r2 = new IntLock(2);

            Thread t1 = new Thread(r1);
            Thread t2 = new Thread(r2);

            t1.start();
            t2.start();

            // 休眠5秒的过程中处于死锁状态
            Thread.sleep(3000);

            t2.interrupt();
        }
    }


    /**
     * 2. 锁申请等待限时,
     *
     * t1和t2线程, t1先run并在5s内拿到lock 锁, 随后睡眠6s, 此时t2来拿lock锁, 由于被t1线程占用了6秒, 超过了等待时间, 所以获取锁失败
     *
     * tryLock也可以不携带参数: 表示在执行方法的一瞬间, 能拿到锁就能拿到不能拿到也不会阻塞
     */
    public static class TimeLock implements Runnable {

        public static ReentrantLock lock = new ReentrantLock();

        @Override
        public void run() {
            try {
                if (lock.tryLock(5, TimeUnit.SECONDS)) {
                    Thread.sleep(6000);
                } else {
                    System.out.println(Thread.currentThread().getName() + " Got lock failed");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }

        public static void main(String[] args) {
            TimeLock tl = new TimeLock();
            Thread t1 = new Thread(tl, "t1");
            Thread t2 = new Thread(tl, "t2");

            t1.start();
            t2.start();
        }
    }


    /**
     * 3. 公平锁
     *
     * 构造方式传入 true 即可创建公平的重入锁,
     *
     * 构建公平锁的成本非常高:
     *   因为需要维护队列
     */
    public static class FailLock implements Runnable {

        public static ReentrantLock lock = new ReentrantLock(true);


        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    System.out.println(Thread.currentThread().getName() + " 获得锁");
                } finally {
                    lock.unlock();
                }
            }
        }

        public static void main(String[] args) {
            FailLock failLock = new FailLock();
            Thread t1 = new Thread(failLock, "t1");
            Thread t2 = new Thread(failLock, "t2");

            t1.start();
            t2.start();
        }
    }
}

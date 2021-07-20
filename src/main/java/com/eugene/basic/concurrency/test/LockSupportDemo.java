package com.eugene.basic.concurrency.test;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class LockSupportDemo implements Runnable {

    private ReentrantLock rt;

    public LockSupportDemo(ReentrantLock rt) {
        this.rt = rt;
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + "开始");
        rt.lock();
        System.out.println(Thread.currentThread().getName() + "获取锁");
        // 将当前线程挂起
        LockSupport.park();
        System.out.println(Thread.currentThread().getName() + "park 结束");
        if (Thread.interrupted()) {
            System.out.println(Thread.currentThread().getName() + "中断了一下");
        }
        rt.unlock();
        System.out.println(Thread.currentThread().getName() + "结束了");
    }

    /**
     * 期望结果：
     *   线程1先获取锁，然后内部调用park方法被挂起了
     *   然后线程2来抢占锁，抢占锁失败。
     *     此阶段的日志为：
     *       线程1开始
     *       线程1获取锁
     *       线程1被挂起
     *       线程2开始
     *       线程2unpark
     *       线程1 interrupt --> 线程1的park操作立即返回
     *       线程1park结束
     *       线程1中断了一下
     *       线程1结束了
     *       线程2获取锁
     *       线程2被挂起
     *
     */
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock rt = new ReentrantLock();
        Thread thread1 = new Thread(new LockSupportDemo(rt), "thread-1");
        thread1.start();
        Thread.sleep(100);
        Thread thread2 = new Thread(new LockSupportDemo(rt), "thread-2");
        thread2.start();

        LockSupport.unpark(thread2);
        System.out.println("当前线程: " + Thread.currentThread().getName() + "打印: thread-2 unpark");
        thread1.interrupt();
        System.out.println("当前线程: " + Thread.currentThread().getName() + "打印: thread-1 interrupt");
    }
}

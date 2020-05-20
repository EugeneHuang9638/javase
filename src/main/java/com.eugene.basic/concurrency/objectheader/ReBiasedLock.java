package com.eugene.basic.concurrency.objectheader;

import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 证明偏向锁的重偏向
 */
public class ReBiasedLock {

    static List<Object> locks = new ArrayList<>();

    static final int THREAD_COUNT = 19;

    public static void main(String[] args) throws InterruptedException {
        // 延迟4.1秒，等待jvm偏向锁功能开启
        Thread.sleep(4300);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < THREAD_COUNT; i++) {
                Object lock = new Object();
                locks.add(lock);
                synchronized (lock) {
                    System.out.println("线程1 第 " + i + " 把锁");
                    System.out.println(ClassLayout.parseInstance(lock).toPrintable());
                    System.out.println("\n *********************************** \n");
                }

            }
        }, "线程1");

        t1.start();
        // 等t1执行完
        t1.join();

        new Thread(() -> {
            for (int i = 0; i < locks.size(); i++) {
                Object lock = locks.get(i);
                synchronized (lock) {
                    System.out.println("线程2 第 " + i + " 把锁");
                    System.out.println(ClassLayout.parseInstance(lock).toPrintable());
                    System.out.println("\n ==================================== \n");
                }
            }
        }, "线程1").start();

    }
}

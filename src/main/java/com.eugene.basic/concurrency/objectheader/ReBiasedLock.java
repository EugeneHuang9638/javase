package com.eugene.basic.concurrency.objectheader;

import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 证明偏向锁的重偏向
 */
public class ReBiasedLock {

    static List<User> locks = new ArrayList<>();

    static final int THREAD_COUNT = 19;

    public static void main(String[] args) throws InterruptedException {
        // 延迟4.1秒，等待jvm偏向锁功能开启
        Thread.sleep(4300);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < THREAD_COUNT; i++) {
                User lock = new User();
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

        // 添加一个新线程，防止出现偏向锁的id重复的情况
        // 我也不知道为什么，只知道这样能解决这样的问题
        Thread tmp = new Thread(() -> {
            System.out.println(1);
        });
        tmp.start();
        tmp.join();

        new Thread(() -> {
            for (int i = 0; i < locks.size(); i++) {
                User lock = locks.get(i);
                synchronized (lock) {
                    System.out.println("线程2 第 " + i + " 把锁");
                    System.out.println(ClassLayout.parseInstance(lock).toPrintable());
                    System.out.println("\n ==================================== \n");
                }
            }
        }, "线程1").start();

    }
}

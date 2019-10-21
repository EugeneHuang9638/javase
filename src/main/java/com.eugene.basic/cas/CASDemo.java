package com.eugene.basic.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Compare and swap/set
 */
public class CASDemo {

    private static volatile int m = 0;

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void increase() {
        // 反编译后执行这条语句需要三条指令
        m++;
    }

    public static void increase2() {
        // 反编译后执行这条语句需要一条指令
        atomicInteger.incrementAndGet();
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 20; i++) {
            Thread inner = new Thread(() -> {
                CASDemo.increase();
            });
            inner.start();
            inner.join();
        }

        // m的结果 <= 20, 因为volatile不能保证原子性
        System.out.println(m);

        for (int i = 0; i < 20; i++) {
            Thread inner = new Thread(() -> {
                CASDemo.increase2();
            });
            inner.start();
            inner.join();
        }
        System.out.println(atomicInteger);

    }

}

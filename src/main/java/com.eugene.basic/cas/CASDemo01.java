package com.eugene.basic.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * CAS: Compare and swap/set
 *   一种无锁的原子性算法, 是一种乐观锁
 *   主要实现原理是开发人员自定义一个期望结果, 若对象的当前值与期望结果不匹配的话
 *   则不进行原子性操作, 否则进行原子性操作。
 *   主要包含:
 *     AtomicInteger和AtomicStampedReference两种
 *     前者只比较期望结果,ABA场景时也会更新成功
 *     后者比较期望结果和期望版本号双重校验, ABA场景因为有版本号的校验, 所以不会成功。
 *
 *
 *     所谓ABA就是一个变量被三个线程操作了, 因为只跟期望值进行比较最后变量又回到原来的
 *     值了.
 *
 */
public class CASDemo01 {

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
                CASDemo01.increase();
            });
            inner.start();
            inner.join();
        }

        // m的结果 <= 20, 因为volatile不能保证原子性
        System.out.println(m);

        for (int i = 0; i < 20; i++) {
            Thread inner = new Thread(() -> {
                CASDemo01.increase2();
            });
            inner.start();
            inner.join();
        }
        System.out.println(atomicInteger);

    }

}

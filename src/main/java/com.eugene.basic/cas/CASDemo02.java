package com.eugene.basic.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 测试CAS  ABA情景
 *
 * 几个CAS操作, 最终的结果就像是忽略了B这一步骤, 但是检测不到中间步骤变化
 *
 * 为了检测到中间步骤的变化, 出现了AtomicStampedReference类, 它是基于期望版本号和期望值来判别,
 * 如果它的版本号时间戳发生了变化, 则表示它变化了
 *
 * CAS应用场景:
 *   1. 应用于多线程简单的数据计算
 *   2. 适合线程冲突少的场景？？？、
 */
public class CASDemo02 {

    private static AtomicInteger atomicInteger = new AtomicInteger(100);

    public static void testAtomicABA() {
        new Thread(() -> {
            // 如果atomicInteger的期望值与100相同的话, 就把它修改成105
            System.out.println(atomicInteger.compareAndSet(100, 105));
            System.out.println(atomicInteger.get());
        }).start();


        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println(atomicInteger.compareAndSet(105, 110));
                System.out.println(atomicInteger.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println(atomicInteger.compareAndSet(110, 100));
                System.out.println(atomicInteger.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 表示初始值100 对应的版本号是1
    private static AtomicStampedReference atomicStampedReference = new AtomicStampedReference(100, 1);

    public static void testAtomicStampedRef() {
        /*new Thread(() -> {
            // 期望值, 新值, 期望版本, 新版本
            System.out.println(atomicStampedReference.compareAndSet(100, 110, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1));
            System.out.println(atomicStampedReference.compareAndSet(110, 120, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1));
        }).start();*/

        new Thread(() -> {
            try {
                int currentStamped = atomicStampedReference.getStamp();
                TimeUnit.SECONDS.sleep(2);
                // 期望值, 新值, 期望版本, 新版本
                System.out.println(atomicStampedReference.compareAndSet(100, 110, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1));
                System.out.println(atomicStampedReference.compareAndSet(110, 120, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                int currentStamped = atomicStampedReference.getStamp();
                System.out.println("currentStamped: " + currentStamped);
                TimeUnit.SECONDS.sleep(3);
                // 期望值, 新值, 期望版本, 新版本
                System.out.println(atomicStampedReference.compareAndSet(120, 110, currentStamped, currentStamped + 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        //testAtomicABA();
        testAtomicStampedRef();
    }

}

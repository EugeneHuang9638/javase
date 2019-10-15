package com.eugene.basic.thread;


import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

/**
 * 使用线程实现银行排队叫号功能, 并分析其运行原理
 *
 *
 * JDK < 1.6: synchronized 是一把重量级锁, 会调用底层操作系统的方法实现同步
 * JDK >= 1.7 synchronized进行了优化, 添加了偏向锁和轻量级锁
 *    偏向锁: 表示若无线程进行抢占资源, 会偏向于第一次占用锁的线程，若下次再次获取到锁执行时, 发现是同一个线程, 此时则继续执行,
 *           若发现是其它线程占用了, 那么会上升成一把轻量级锁(会采用CAS算法 compare and set: 适用于线程不激烈的情景)
 *    轻量级锁: 线程有交替使用, 即线程间按照某种规则顺序来运行
 *    重量级锁: 调用了操作系统的某些函数实现同步
 *    自旋锁: 当前线程竞争失败的时候, 不马上转换级别(线程级别的转换很耗时, 只能由低到高), 而是执行几次空循环(相当于延迟时间),
 *           当执行完这些空循环后, 也许当前线程就拿到锁了。
 *    锁消除: JIT(java 编译时环境)在编译的时候把他认为不必要的锁取消了, 它会把synchronized这把锁给取消。
 *           eg:
 *              sychronized(this) {
 *                  int i = 0;
 *              }
 *
 *
 * java中任何一个实例对象都包含: 对象头、实例数据、填充数据
 *   (整体大小是8的倍数, 若对象头(64位jdk是12个byte) + 实例数据的总大小不是8的倍数那么填充数据会把它填充，变成8的倍数)
 *
 * synchronized加锁的原理就是对加锁对象的对象头的某一个标志位进行修改, 用来标识该对象被其它线程占有了
 *
 *
 * 对象头一共存储了三个部分的数据:
 *   1. mark word  =>   存储对象的hashCode(标识对象的, 当字符串equals时, 会获取到hashCode进行判断)或锁信息
 *   2. class metadate address
 *   3. array length
 *
 *   一个对象头总共占32bit。 25bit(是对象的hashCode), 4bit是对象分代年龄，  1bit判断是否是偏向锁， 2bit是锁标志位
 */
public class TicketThread extends Thread {

    private static int index = 1;
    private static final int MAX_INDEX = 506;

    public TicketThread(String name) {
        super.setName(name);
    }

    @Override
    public void run() {
        while(index <= MAX_INDEX) {
            // 使用某个类的Class类作为锁 则表示该类的所有对象都适用这把锁
            synchronized(TicketThread.class) {
                try {
                    if (index <= MAX_INDEX) {
                        //
                        TimeUnit.MINUTES.sleep(2);
                        System.out.println(Thread.currentThread().getName() + "线程叫号: " + index++);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void testSyncMethod() {

    }

    public static void main(String[] args) {
        new TicketThread("1").start();
        new TicketThread("2").start();
        new TicketThread("3").start();
        new TicketThread("4").start();
    }
}



class A {
    private boolean flag;

    public static void main(String[] args) {
        System.out.println(ClassLayout.parseInstance(new A()).toPrintable());

        /**
         * com.eugene.basic.thread.A object internals:
         *  OFFSET  SIZE      TYPE DESCRIPTION                               VALUE
         *       0     4           (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
         *       4     4           (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4           (object header)                           05 c1 00 20 (00000101 11000001 00000000 00100000) (536920325)
         *      12     1   boolean A.flag                                    false
         *      13     3           (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 3 bytes external = 3 bytes total
         *
         * 对象头默认大小12byte, 一个boolean类型数据大小位1byte, 所以对象头 + 实例数据总大小位 13btye,
         * 此时不是8的倍数, 所以填充数据会填充3byte变成16byte(因为16是8的倍数),
         * 所以类A的实例对象的内存大小位16byte
         */
    }
}
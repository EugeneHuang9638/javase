package com.eugene.basic.concurrency.objectheader;

import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试重撤销
 *
 * 所谓重撤销也叫重偏向，也叫重轻量，
 * 因为在偏向锁升级为轻量锁的过程中，需要对撤销锁，变为无锁状态才行。
 *
 *
 * 而之前说过，当同一类型的锁升级为轻量锁的次数超过20时，会发生重偏向。
 *
 * 同理，当同一类型的重偏向锁升级轻量锁次数超过40(包括上面说的20)时，会发生重撤销，
 * 即批量将后续的锁批量撤销，然后升级为轻量锁
 *
 */
public class ReLightweightLock {

    static List<User> locks = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting");

        // 延迟加载，让jvm开启偏向锁功能
        Thread.sleep(4400);


        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 45; i++) {
                User lock = new User();
                locks.add(lock);
                synchronized (lock) {
                    // 不做任何事，可以确定45把锁全部变成了偏向锁
                }
            }
        }, "t1");
        t1.start();
        t1.join();

        // 打印第43把锁，已经是偏向锁了
        System.out.println("i = 42 \t" + ClassLayout.parseInstance(locks.get(42)).toPrintable());

        // 创建一个新线程睡眠2s，保证下面的代码先执行，保证重偏向时，不会出现线程ID重复的情况
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "tmp1").start();

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < locks.size(); i++) {
                User lock = locks.get(i);
                synchronized (lock) {
                    if (i == 10 || i == 21) {
                        // 输出第11和22个，看看分别是不是轻量锁和偏向锁
                        System.out.println("t2 i = " + i + "\t" + ClassLayout.parseInstance(lock).toPrintable());
                    }
                }
            }
        }, "t1");
        t2.start();
        t2.join();

        // 查看第11把锁对象，看看是不是20之前的锁也被重偏向了  --> 结果证明，只会对20以后的锁重偏向
        // 这里输出的是无锁状态，因为i= 10时，被线程2持有过，膨胀成轻量锁了，而轻量锁在释放锁后会变成无锁状态
        System.out.println("i = 10\t" + ClassLayout.parseInstance(locks.get(10)).toPrintable());

        // 查看第43把锁对象，看看是不是被批量重偏向了  --> 结果证明：是的
        System.out.println("i = 42\t" + ClassLayout.parseInstance(locks.get(42)).toPrintable());


        // 创建一个新线程睡眠2s，保证下面的代码先执行，保证重偏向时，不会出现线程ID重复的情况
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "tmp2").start();

        Thread t3 = new Thread(() -> {
            for (int i = 0; i < locks.size(); i++) {
                User lock = locks.get(i);
                synchronized (lock) {
                    if (i == 10 || i == 21 || i == 40) {
                        // 输出第11和22个，看看是不是都为轻量锁
                        // ---> 结果证明：都为轻量锁
                        // i == 10为轻量锁，我们都能理解，因为偏向锁被其他线程持有了，当然膨胀为轻量锁了
                        // 可是i == 21不应该为偏向锁么？(超过了重偏向的阈值)
                        // ==> 这里不是重偏向了，因为user类型的锁升级为轻量锁的次数达到了40(线程2升级了20次)，
                        // 所以jvm直接做了重轻量的操作，把后面所有的锁都变成轻量锁了
                        // 所以i == 21应该是轻量锁
                        // i == 40同样也是轻量锁
                        System.out.println("t3 i = " + i + "\t" + ClassLayout.parseInstance(lock).toPrintable());
                    }
                }
            }
        }, "t3");
        t3.start();
        t3.join();

        // 此时是无锁状态，因为线程3进行批量重轻量了，而它释放了锁，所以是无锁状态
        System.out.println("main i = 40 \t" + ClassLayout.parseInstance(locks.get(40)).toPrintable());

    }
}

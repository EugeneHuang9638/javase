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

    static List<User> users = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting");

        // 延迟加载，让jvm开启偏向锁功能
        Thread.sleep(4400);


        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                User lock = new User();
                users.add(lock);
                synchronized (lock) {
                    if (i == 22) {
                        System.out.println("t1 i = " + i + "\t" + ClassLayout.parseInstance(lock).toPrintable());
                    }
                }
            }
        });
        t1.start();
        t1.join();

        // 打印第88个，已经是偏向锁了
        System.out.println("i = 88 \t" + ClassLayout.parseInstance(users.get(88)).toPrintable());

        // 创建一个新线程睡眠2s，保证下面的代码先执行，保证重偏向时，不会出现线程ID重复的情况
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < users.size(); i++) {
                User lock = users.get(i);
                synchronized (lock) {
                    if (i == 10 || i == 21) {
                        // 输出第10和21个，看看分别是不是轻量锁和偏向锁
                        System.out.println("t2 i = " + i + "\t" + ClassLayout.parseInstance(lock).toPrintable());
                    }
                }
            }
        });
        t2.start();
        t2.join();

        // 查看第10个锁对象，看看是不是20之前的锁也被重偏向了  --> 结果证明，只会对20以后的锁重偏向
        System.out.println("i = 10\t" + ClassLayout.parseInstance(users.get(10)).toPrintable());

        // 查看第89个锁对象，看看是不是被批量重偏向了  --> 结果证明：是的
        System.out.println("i = 88\t" + ClassLayout.parseInstance(users.get(88)).toPrintable());


        // 创建一个新线程睡眠2s，保证下面的代码先执行，保证重偏向时，不会出现线程ID重复的情况
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Thread t3 = new Thread(() -> {
            for (int i = 0; i < users.size(); i++) {
                User lock = users.get(i);
                synchronized (lock) {
                    if (i == 10 || i == 21) {
                        // 输出第10和21个，看看是不是都为轻量锁
                        // ---> 结果证明：都为轻量锁
                        // i == 10为轻量锁，我们都能理解，因为偏向锁被其他线程持有了，当然膨胀为轻量锁了
                        // 可是i == 21不应该为偏向锁么？因为进行重偏向了
                        // 这里不是重偏向了，因为user类型的锁升级为轻量锁的次数达到了40，所以jvm直接
                        // 做了重撤销或者说重轻量的操作，把后面所有的锁都变成轻量锁了
                        // 又因为前面20次本来就是轻量锁，所以此时整个100个user对象都是轻量锁
                        System.out.println("t3 i = " + i + "\t" + ClassLayout.parseInstance(lock).toPrintable());
                    }
                }
            }
        });
        t3.start();
        t3.join();

    }

}

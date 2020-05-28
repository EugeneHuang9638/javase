package com.eugene.basic.concurrency.objectheader;

import org.openjdk.jol.info.ClassLayout;

public class ValidWait {

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(4100);

        final User user = new User();

        System.out.println("before lock");
        System.out.println(ClassLayout.parseInstance(user).toPrintable());

        Thread t1 = new Thread(() -> {
            synchronized (user) {
                System.out.println("lock ing");
                System.out.println("before wait");
                System.out.println(ClassLayout.parseInstance(user).toPrintable());

                try {
                    user.wait();
                    System.out.println("after wait");
                    System.out.println(ClassLayout.parseInstance(user).toPrintable());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }, "t1");
        t1.start();

        // 主线程睡眠3s后，唤醒t1线程
        Thread.sleep(3000);
        System.out.println("主线程查看锁，变成了重量锁");
        System.out.println(ClassLayout.parseInstance(user).toPrintable());
    }
}

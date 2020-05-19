package com.eugene.basic.concurrency.objectheader;

import org.openjdk.jol.info.ClassLayout;

import java.nio.ByteOrder;

/**
 * 验证对象头hashCode信息
 */
public class Valid {


    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(4100);

        System.out.println(ByteOrder.nativeOrder().toString());

        User user = new User();
        System.out.println("before lock");
        System.out.println(ClassLayout.parseInstance(user).toPrintable());


        synchronized (user) {
            System.out.println("lock ing");
            System.out.println(ClassLayout.parseInstance(user).toPrintable());
        }

        System.out.println("after lock");

        System.out.println(ClassLayout.parseInstance(user).toPrintable());
    }
}

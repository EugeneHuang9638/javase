package com.eugene.basic.concurrency.objectheader;

import org.openjdk.jol.info.ClassLayout;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 证明重量级锁
 */
public class ValidSynchronized {

    static Object lock = new Object();

    static volatile LinkedList<String> queue = new LinkedList<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("before lock");
        System.out.println(ClassLayout.parseInstance(lock).toPrintable());

        Consumer consumer = new Consumer();
        Producer producer = new Producer();

        consumer.start();
        producer.start();

        Thread.sleep(500);
        consumer.interrupt();
        producer.interrupt();

        // 睡眠3s ==> 目的是为了让锁自己释放，防止在释放过程中打印锁的状态出现重量锁的情况
        Thread.sleep(3000);
        System.out.println("after lock");
        System.out.println(ClassLayout.parseInstance(lock).toPrintable());
    }
}


class Producer extends Thread {


    @Override
    public void run() {
        while (!isInterrupted()) {
            synchronized (ValidSynchronized.lock) {
                System.out.println("lock ing");
                System.out.println(ClassLayout.parseInstance(ValidSynchronized.lock).toPrintable());
                String message = UUID.randomUUID().toString();
                System.out.println("生产者生产消息：" + message);
                ValidSynchronized.queue.offer(message);
                try {
                    // 生产者自己wait，目的是释放锁
                    ValidSynchronized.lock.notify();
                    ValidSynchronized.lock.wait();
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    // main方法中对producer线程进行中断了
                    // 就会抛出InterruptedException异常。
                    // 但这有个坑，就是外部调用了当前线程的interrupt()
                    // 方法后，会抛出InterruptedException异常，
                    // 同时，当前线程的中断标识位就又置为false了
                    // 因为循环的条件是当前线程不是中断状态,
                    // 我们为了让Producer线程停止，所以catch中还调用了
                    // 当前线程的interrupt()方法
                    this.interrupt();
                }
            }
        }
    }
}

class Consumer extends Thread {

    @Override
    public void run() {
        while (!isInterrupted()) {
            synchronized (ValidSynchronized.lock) {
                if (ValidSynchronized.queue.size() == 0) {
                    try {
                        ValidSynchronized.lock.wait();
                        ValidSynchronized.lock.notify();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String message = ValidSynchronized.queue.pollLast();
                System.out.println("消费者消费消息：" + message);

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    this.interrupt();
                }
            }
        }
    }
}

package com.eugene.basic.thread.productandconsumerblockingqueue;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 使用阻塞队列来实现消费者生产者案例
 *
 * 涉及到的知识点：
 * volatile + atomic(cas) + blockingQueue
 *
 * 优点: 不需要手动的去调度具体的线程、通知具体的线程，将这套繁琐的操作交由阻塞队列去做，
 * 阻塞队列天生自带 等待同步插入数据、等待同步获取数据的功能。
 *
 */
public class Core {

    public static void main(String[] args) throws InterruptedException {
        ShareData shareData = new ShareData(new ArrayBlockingQueue(1));
        new Thread(() -> {
            try {
                shareData.product();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "生产者").start();

        new Thread(() -> {
            try {
                shareData.consumer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "消费者").start();

        Thread.sleep(10000);
        System.out.println("生产者和消费者协调工作10s后，main线程结束生产和消费工作");
        shareData.stop();
    }
}

class ShareData {
    // 用来控制消费者和生产者的执行
    private volatile boolean flag = true;

    // 生产者和消费者要处理的消息，保证每次只有一个线程操作这个变量，保证线程安全
    private AtomicStampedReference<String> atomicStampedReference = new AtomicStampedReference<>(null, 0);

    BlockingQueue<String> blockingQueue;

    public ShareData(BlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println("使用到的阻塞队列：" + blockingQueue.getClass().getName());
    }

    public void product() throws InterruptedException {
        while (flag) {
            String message = UUID.randomUUID().toString();
            int stamp = atomicStampedReference.getStamp();
            boolean compareAndSet = atomicStampedReference.compareAndSet(atomicStampedReference.getReference(), message, stamp, stamp + 1);
            if (compareAndSet) {
                // 如果达到阻塞队列的最大值，生产每个消息耗费两秒钟
                boolean offer = blockingQueue.offer(message, 2, TimeUnit.SECONDS);
                if (offer) {
                    System.out.println(Thread.currentThread().getName() + "\t 生产消息放入队列, 消息：" + message);
                } else {
                    System.out.println(Thread.currentThread().getName() + "\t 生产消息失败, 消息：" + message);
                }
            }

            Thread.sleep(1000);
        }
    }

    public void consumer() throws InterruptedException {
        while (flag) {
            // 如果阻塞队列中没有值，则消费消息的最大等待时间为2s
            String poll = blockingQueue.poll(2, TimeUnit.SECONDS);
            if (poll != null) {
                System.out.println(Thread.currentThread().getName() + "\t 消费消息成功, 消息：" + poll);
            } else {
                // 一定要放在poll的后面来，同时要保证消费者消费完了最后一条消息
                if (!flag) {
                    System.out.println("生产者消费消息结束，消费者消费消息也结束");
                    break;
                }

                System.out.println(Thread.currentThread().getName() + "\t 消费消息失败, 无消息可消费");
            }

            Thread.sleep(1000);
        }
    }

    public void stop() {
        this.flag = false;
    }

}

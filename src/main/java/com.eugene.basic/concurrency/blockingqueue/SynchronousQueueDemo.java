package com.eugene.basic.concurrency.blockingqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * SynchronousQueue 只有一个容量，
 * 一对一服务，0库存，最多存一个元素，
 *
 * 你不消费，我不生产
 *
 */
public class SynchronousQueueDemo {

    public static void main(String[] args) {
        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();

        new Thread(() -> {
            try {
                blockingQueue.put("a");
                System.out.println("线程A生产：a");
                // 因为put是阻塞式的添加，因此只有队列中的a元素被消费完了，b才能被put进去
                blockingQueue.put("b");
                System.out.println("线程A生产：b");
                blockingQueue.put("c");
                System.out.println("线程A生产：c");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, "线程A").start();

        new Thread(() -> {
            try {
                // 线程b每隔2s消费一次
                System.out.println("线程B消费: " + blockingQueue.take());
                Thread.sleep(2000);

                System.out.println("线程B消费: " + blockingQueue.take());
                Thread.sleep(2000);

                System.out.println("线程B消费: " + blockingQueue.take());
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, "线程B").start();

    }
}

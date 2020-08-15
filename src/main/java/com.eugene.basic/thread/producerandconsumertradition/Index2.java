package com.eugene.basic.thread.producerandconsumertradition;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// 2个生产者消费消息，5个消费者抢消息进行消费
public class Index2 {

    private static LinkedList<Message> linkedList = new LinkedList<>();

    private static Object consumerLock = new Object();

    private static Object producerLock = new Object();

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                while (true) {
                    synchronized (consumerLock) {
                        if (linkedList.size() <= 0) {
                            continue;
                        }

                        Message first = linkedList.pollLast();
                        System.out.println(Thread.currentThread().getName() + "消费消息" + first.content);
                    }
                }
            }, "消费者-" + i).start();
        }

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                while (true) {
                    synchronized (producerLock) {
                        producerLock.notify();
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message message = new Message(UUID.randomUUID().toString());
                        linkedList.addFirst(message);
                        System.out.println(Thread.currentThread().getName() + "生产消息" + message.content);
                        try {
                            producerLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "生产者-" + i).start();
        }
    }
}

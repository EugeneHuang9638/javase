package com.eugene.basic.thread.producerandconsumer;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 使用wait和notify实现一个消息队列，
 * 生产者1秒生产一个消息
 * 当队列的元素达到10个时，生产者停止生产，通知消费者消费消息
 *
 * 可以发现： notify和wait和synchronized基本上是成对存在的
 * 同时wait时会释放当前的锁(要调用锁的wait方法)
 * notify是随机唤醒一个wait的线程
 */
public class Index1 {

    private LinkedList<Message> linkedList = new LinkedList<>();

    private static Object lock = new Object();


    public static void main(String[] args) {
        Index1 index1 = new Index1();

        new Thread(() -> {

            while (true) {
                synchronized (lock) {
                    if (index1.linkedList.size() <= 0) {
                        try {
                            // 通知生产者继续生产
                            lock.notify();
                            // 这里有个知识点：
                            // 当调用lock的wait方法时，当前线程会
                            // 释放lock这把锁
                            lock.wait();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    Message message = index1.linkedList.pollLast();
                    System.out.println("消费消息： " + message.content);
                }
            }

        }, "消费者").start();


        new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    if (index1.linkedList.size() == 10) {
                        try {
                            System.out.println("生产者生产消息达到10条，开始通知消费者消费");
                            lock.notify();

                            // 自己等待，释放这把锁让消费者拥有锁，停止自己的生产
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message message = new Message(UUID.randomUUID().toString());
                    index1.linkedList.addFirst(message);
                    System.out.println("生产者生产消息： " + message.content);
                }

            }
        }, "生产者").start();

    }

}

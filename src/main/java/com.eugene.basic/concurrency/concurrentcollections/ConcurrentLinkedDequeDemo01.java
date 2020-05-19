package com.eugene.basic.concurrency.concurrentcollections;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * ConcurrentLinkedQueue: 非阻塞式集合(当集合中为空或者满了时不会让调用者(调用集合操作的线程)阻塞)
 *
 * 启动一个线程添加大量数据至集合中
 * 启动另外一个线程移除数据
 *
 */
public class ConcurrentLinkedDequeDemo01 {

    /**
     * 与普通集合一样有add等方法,
     * 但它有如下方法
     *   offer(E e): 将指定元素放到尾部
     *   poll(): 获取并移除此队列的头, 如果队列为空, 则返回Null
     *
     */
    private static ConcurrentLinkedDeque<String> concurrentLinkedDeque = new ConcurrentLinkedDeque<>();


    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 100; i++) {
            Thread inner = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    concurrentLinkedDeque.add(Thread.currentThread().getName() + "j");

                }
            });

            inner.start();
            inner.join();
        }

        System.out.println("After added size = " + concurrentLinkedDeque.size());


        for (int i = 0; i < 100; i++) {
            Thread inner = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    // 移除队头元素  ==  pollFirst()
                    concurrentLinkedDeque.poll();

                    // 移除队尾元素
                    concurrentLinkedDeque.pollLast();
                }
            });

            inner.start();
            inner.join();
        }

        System.out.println("After removed size = " + concurrentLinkedDeque.size());
    }
}

package com.eugene.basic.concurrency.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 测试阻塞队列抛异常相关api
 */
public class BlockingQueueDemo1 {

    public static void main(String[] args) {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue(3);
        System.out.println(blockingQueue.add("a"));
        System.out.println(blockingQueue.add("b"));
        System.out.println(blockingQueue.add("c"));

        //testAddWithQueueFull(blockingQueue);

        testRemoveWithQueueEmpty(blockingQueue);
    }

    /**
     * 测试队列满了后，添加元素抛异常
     * @param blockingQueue
     */
    private static void testAddWithQueueFull(BlockingQueue<String> blockingQueue) {


        /**
         * 抛出队列满的异常(状态不合法)
         *
         * java.lang.IllegalStateException: Queue full
         * 	at java.util.AbstractQueue.add(AbstractQueue.java:98)
         * 	at java.util.concurrent.ArrayBlockingQueue.add(ArrayBlockingQueue.java:312)
         * 	at com.eugene.basic.concurrency.blockingqueue.BlockingQueueDemo1.main(BlockingQueueDemo1.java:17)
         */
        System.out.println(blockingQueue.add("d"));
    }

    /**
     * 测试队列为空时，还在remove
     * @param blockingQueue
     */
    private static void testRemoveWithQueueEmpty(BlockingQueue<String> blockingQueue) {
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());

        /**
         * 抛出NoSuchElementException异常
         *
         *Exception in thread "main" java.util.NoSuchElementException
         * 	at java.util.AbstractQueue.remove(AbstractQueue.java:117)
         * 	at com.eugene.basic.concurrency.blockingqueue.BlockingQueueDemo1.testRemoveWithQueueEmpty(BlockingQueueDemo1.java:52)
         * 	at com.eugene.basic.concurrency.blockingqueue.BlockingQueueDemo1.main(BlockingQueueDemo1.java:19)
         */
        System.out.println(blockingQueue.remove());
    }
}

package com.eugene.basic.concurrency.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 测试阻塞队列特殊值相关api
 */
public class BlockingQueueDemo2 {

    public static void main(String[] args) {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue(3);
        System.out.println(blockingQueue.offer("a"));
        System.out.println(blockingQueue.offer("b"));
        System.out.println(blockingQueue.offer("c"));

        // 探测器，拿到第一个队列的元素
        System.out.println(blockingQueue.peek());

        // 测试时，两者方法选一个运行
        testAddWithQueueFull(blockingQueue);
//        testRemoveWithQueueEmpty(blockingQueue);
    }

    /**
     * 测试队列满了后，添加元素抛异常
     * @param blockingQueue
     */
    private static void testAddWithQueueFull(BlockingQueue<String> blockingQueue) {


        /**
         * 使用offer插入，不抛出异常, 而是返回true和false
         */
        System.out.println(blockingQueue.offer("d"));
    }

    /**
     * 测试队列为空时，还在remove
     * @param blockingQueue
     */
    private static void testRemoveWithQueueEmpty(BlockingQueue<String> blockingQueue) {
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());

        /**
         * 使用poll移除队列，如果队列为空，不抛出异常，而是返回null
         */
        System.out.println(blockingQueue.poll());
    }
}

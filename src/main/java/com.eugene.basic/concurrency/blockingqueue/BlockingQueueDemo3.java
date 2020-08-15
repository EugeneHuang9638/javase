package com.eugene.basic.concurrency.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 测试阻塞队列阻塞相关api
 */
public class BlockingQueueDemo3 {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue(3);
        blockingQueue.put("a");
        blockingQueue.put("b");
        blockingQueue.put("c");

        // 测试时，两者方法选一个运行
        testBlockingAddWithQueueFull(blockingQueue);
//        testBlockingRemoveWithQueueEmpty(blockingQueue);
    }

    /**
     * 测试队列满了后，添加元素抛异常
     * @param blockingQueue
     */
    private static void testBlockingAddWithQueueFull(BlockingQueue<String> blockingQueue) throws InterruptedException {


        /**
         * 使用put插入，不返回值，也不抛异常, 但是一直阻塞者，等有空位时才解阻塞
         */
        blockingQueue.put("d");
    }

    /**
     * 测试队列为空时，还在remove
     * @param blockingQueue
     */
    private static void testBlockingRemoveWithQueueEmpty(BlockingQueue<String> blockingQueue) throws InterruptedException {
        blockingQueue.take();

        /**
         * 使用take移除队列，不返回值，也不抛异常, 但是一直阻塞者，等有值移除时才解阻塞
         */
        blockingQueue.take();
    }
}

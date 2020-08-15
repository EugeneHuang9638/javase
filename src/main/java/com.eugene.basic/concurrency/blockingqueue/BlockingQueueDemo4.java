package com.eugene.basic.concurrency.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 测试阻塞队列延迟阻塞相关api
 */
public class BlockingQueueDemo4 {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue(3);
        System.out.println(blockingQueue.offer("a", 3, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("b", 3, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("c", 3, TimeUnit.SECONDS));

        // 测试时，两者方法选一个运行
        testDelayBlockingAddWithQueueFull(blockingQueue);
//        testDelayBlockingRemoveWithQueueEmpty(blockingQueue);
    }

    /**
     * 测试队列满了后，添加元素抛异常
     * @param blockingQueue
     */
    private static void testDelayBlockingAddWithQueueFull(BlockingQueue<String> blockingQueue) throws InterruptedException {


        /**
         * 使用offer + 超时时间 插入，若3s内，队列中的元素还是满的，则放弃插入
         */
        System.out.println(blockingQueue.offer("d", 3, TimeUnit.SECONDS));
    }

    /**
     * 测试队列为空时，还在remove
     * @param blockingQueue
     */
    private static void testDelayBlockingRemoveWithQueueEmpty(BlockingQueue<String> blockingQueue) throws InterruptedException {

        /**
         * 使用poll + 超时时间 取元素，如果队列中没有元素且3秒内还没有的话，直接放弃
         */
        System.out.println(blockingQueue.poll(3, TimeUnit.SECONDS));
    }
}

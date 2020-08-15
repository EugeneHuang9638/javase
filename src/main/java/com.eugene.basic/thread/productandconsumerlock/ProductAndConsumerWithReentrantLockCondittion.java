package com.eugene.basic.thread.productandconsumerlock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用reentrantLock的condition来实现生产者和消费者问题
 *
 * 一个字段，只能从0自增为1，从1自减为0
 *
 * synchronized <===> ReentrantLock
 * await   <===> condition.await()
 *
 * notifyAll <===> condition.signalAll()
 *
 */
public class ProductAndConsumerWithReentrantLockCondittion {


    public static void main(String[] args) {
        ShareData data = new ShareData();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                data.increment();
            }, "线程" + i).start();
        }

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                data.decrement();
            }, "线程" + i).start();
        }
    }
}

class ShareData {
    private int number;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    /**
     * 生产者操作
     */
    public void increment() {
        try {
            lock.lock();

            // 判断是否阻塞
            while (number != 0) {
                condition.await();
            }
            // 执行业务逻辑
            number++;
            System.out.println(Thread.currentThread().getName() + "线程生产了消息：" + number);

            // 唤醒其他线程
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 消费者操作
     */
    public void decrement() {
        try {
            lock.lock();

            // 判断是否阻塞
            while (number == 0) {
                condition.await();
            }

            // 执行业务逻辑
            number--;
            System.out.println(Thread.currentThread().getName() + "线程消费了消息：" + number);
            // 唤醒其他线程
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

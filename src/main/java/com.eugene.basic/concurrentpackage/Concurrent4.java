package com.eugene.basic.concurrentpackage;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 测试读写锁
 */
public class Concurrent4 {

    public static class ReadWriteLockDemo {

        // 创建重入锁, 与读写锁作比较
        private static Lock lock = new ReentrantLock();

        //创建读写锁对象
        private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        // 获取读写锁对象的读锁
        private static Lock readLock = readWriteLock.readLock();

        // 获取读写锁对象的写锁
        private static Lock writeLock = readWriteLock.writeLock();
        private int value;

        public Object handleRead(Lock lock) throws InterruptedException {
            try {
                lock.lock();
                Thread.sleep(1000);
                return value;
            } finally {
                lock.unlock();
            }
        }

        public void handleWrite(Lock lock, int index) throws InterruptedException {
            try {
                lock.lock();
                Thread.sleep(1000);
                value = index;
            } finally {
                lock.unlock();
            }
        }


        public static void main(String[] args) {
            final ReadWriteLockDemo demo = new ReadWriteLockDemo();

            // 创建读 线程的Runnable对象，并测试拥有不同索时，程序运行速率
            Runnable readRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        // 因为是读锁, 所以所有的读线程是并行运行的
                        demo.handleRead(readLock);
                        //demo.handleRead(lock);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            // 创建写 线程的Runnable对象，并测试拥有不同索时，程序运行速率
            Runnable writeRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        // 因为是写锁, 所有的线程需要等上一个线程执行完毕后才能执行
                        demo.handleWrite(writeLock, new Random().nextInt());
//                        demo.handleWrite(lock, new Random().nextInt());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };


            // 创建18个读线程
            for (int i = 0; i < 18; i++) {
                new Thread(readRunnable).start();
            }

            // 创建2个写线程
            for (int i = 18; i < 20; i++) {
                new Thread(writeRunnable).start();
            }

        }
    }
}

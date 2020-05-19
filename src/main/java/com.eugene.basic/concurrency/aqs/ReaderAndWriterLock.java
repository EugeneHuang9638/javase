package com.eugene.basic.concurrency.aqs;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁: 一个拥有读操作和写操作的锁
 *
 *
 * 锁降级:
 *   在写锁完成快要释放锁之前再添加一把读锁
 *      write.lock();
 *      .......处理逻辑
 *      reader.lock();
 *      write.unlock();
 *
 *
 *  =>  但是具体会使用这个api的例子目前还没找到，待确认
 *
 */
public class ReaderAndWriterLock {

    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private static Lock readLock = readWriteLock.readLock();

    private static Lock writerLock = readWriteLock.readLock();

    private static volatile int value = 0;

    private static void handleWrite(Lock writerLock) {
        try {
            writerLock.lock();
            value++;
            System.out.println("handleWrite : ===> " + value);
        } finally {
            writerLock.unlock();
        }
    }

    private static void handleReader(Lock readLock) {
        try {
            readLock.lock();
            System.out.println("handleReader : ===> " + value);
        } finally {
            readLock.unlock();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            new Thread(()-> {
                handleReader(readLock);
            }, i + "reader").start();

            new Thread(()-> {
                handleWrite(writerLock);
            }, i + "writer").start();
        }
    }
}

package com.eugene.basic.thread.multhreadorderprint;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用ReentrantLock + Condition 实现多个线程按顺序调度
 * 更好理解的版本
 * 线程A打印5次
 * 线程B打印10次
 * 线程C打印15次
 *
 * 循环10次
 *
 * 模板：
 * 1、将所有操作都封装到线程资源类中，完成高内聚、低耦合操作
 * 2、condition的await和signal方法和object wait和notify方法类似，都需要和锁配套使用
 * 3、 判断(非干活条件，则让自己睡眠) + 干活(干自己该做的事情) + 唤醒(唤醒其他的线程)
 *
 */
public class MulThreadOrderPrintWithReentrantLockHigh {

    public static void main(String[] args) {
        ShareData shareData = new ShareData();
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    shareData.print5();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    shareData.print10();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    shareData.print15();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

/**
 * 线程资源类，解耦。
 * 将具体的操作放在线程资源类内中
 */
class ShareData {

    private Lock lock = new ReentrantLock();
    private volatile String thread = "A";
    private Condition threadACondition = lock.newCondition();
    private Condition threadBCondition = lock.newCondition();
    private Condition threadCCondition = lock.newCondition();


    public void print5() throws InterruptedException {
        try {
            lock.lock();
            // 1. 判断
            while (!"A".equals(thread)) {
                threadACondition.await();
            }

            // 2. 干活
            for (int i = 0; i < 5; i++) {
                System.out.println("线程A打印：" + i);
            }

            // 3. 唤醒
            thread = "B";
            threadBCondition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print10() throws InterruptedException {
        try {
            lock.lock();
            // 1. 判断
            while (!"B".equals(thread)) {
                threadBCondition.await();
            }

            // 2. 干活
            for (int i = 0; i < 10; i++) {
                System.out.println("线程B打印：" + i);
            }

            // 3. 唤醒
            thread = "C";
            threadCCondition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print15() throws InterruptedException {
        try {
            lock.lock();
            // 1. 判断
            while (!"C".equals(thread)) {
                threadCCondition.await();
            }

            // 2. 干活
            for (int i = 0; i < 15; i++) {
                System.out.println("线程C打印：" + i);
            }

            // 3. 唤醒
            thread = "A";
            threadACondition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
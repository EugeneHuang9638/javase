package com.eugene.basic.thread.multhreadorderprint;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 与传统版的类似，直接把加锁和wait以及notify部分，改成对应reentrantLock的实现方式即可
 *
 * 总结：这种线程按顺序执行的，肯定是使用同一把锁，并且有volatile变量来控制具体执行的哪个线程
 */
public class MulThreadOrderPrintWithReentrantLock {


    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
    private static volatile String thread = "A";

    public static void main(String[] args) {
        new Thread(() -> {
            int countA = 5;
            while (true) {
                try {
                    lock.lock();
                    if (!"A".equals(thread)) {
                        condition.signalAll();
                        condition.await();
                    }

                    System.out.println("线程A打印");
                    if (--countA == 0) {

                        Thread.sleep(2000);
                        countA = 5;
                        thread = "B";
                        condition.signalAll();
                        condition.await();
                    }
                } catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }).start();

        new Thread(() -> {
            int countB = 10;
            while (true) {
                try {
                    lock.lock();
                    if (!"B".equals(thread)) {
                        condition.signalAll();
                        condition.await();
                    }

                    System.out.println("线程B打印");
                    if (--countB == 0) {

                        Thread.sleep(2000);
                        countB = 10;
                        thread = "C";
                        condition.signalAll();
                        condition.await();
                    }
                } catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }).start();

        new Thread(() -> {
            int countC = 15;
            while (true) {
                try {
                    lock.lock();
                    if (!"C".equals(thread)) {
                        condition.signalAll();
                        condition.await();
                    }

                    System.out.println("线程C打印");
                    if (--countC == 0) {

                        Thread.sleep(2000);
                        countC = 15;
                        thread = "A";
                        condition.signalAll();
                        condition.await();
                    }
                } catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }).start();


    }
}

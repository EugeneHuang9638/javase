package com.eugene.basic.concurrency.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TwoThreadLock {

    static ReentrantLock lock = new ReentrantLock(true);

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                lock.lock();
                System.out.println("Thread a get lock");
                TimeUnit.SECONDS.sleep(60);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "线程a").start();

        Thread t1 = new Thread(() -> {
            try {
                lock.lock();
                System.out.println("Thread t1 get lock");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "线程t1");

        t1.start();
        t1.join();

        System.out.println("end");
    }
}

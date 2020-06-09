package com.eugene.basic.concurrency.aqs;

import java.util.concurrent.locks.ReentrantLock;

public class SimpleThreadLock {

    static ReentrantLock lock = new ReentrantLock(true);

    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(() -> {
            try {
                lock.lock();
                System.out.println("Get lock");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "线程a");

        a.start();
        a.join();
        System.out.println("end");
    }
}

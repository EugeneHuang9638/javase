package com.eugene.basic.concurrency.aqs;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport.park()
 * 内部操作的是Unsafe中的park方法
 */
public class LockSupportDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("start");
            LockSupport.park();
            System.out.println("end");
        });
        thread.start();

        Thread.sleep(3000);
        LockSupport.unpark(thread);
    }
}

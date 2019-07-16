package com.eugene.basic.thread;

/**
 * 回顾线程基础知识
 * 1. 回顾线程创建的两种方式
 */
public class Thread1 {

    public static void main(String[] args) {
        // 1. 通过Thread类创建并启动线程
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                System.out.println("线程: " + Thread.currentThread());
            }
        };
        thread1.start();
        //

        /*************************************/
        // 2. 通过Runnable接口创建线程
        Thread thread2 = new Thread(new Thread1Inner());
        thread2.start();

    }
}

class Thread1Inner implements Runnable {

    public void run() {
        System.out.println("通过Runnable接口创建的线程, " + Thread.currentThread());
    }
}



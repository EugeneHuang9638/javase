package com.eugene.basic.thread;

/**
 * 测试线程join和yield方法
 *
 * join方法: 两个api, 一个是无限制等待另一个是有限制等待
 *
 * join方法本质: 让 "当前线程" 等待 调用join的线程先执行
 */
public class Thread8 {

    public volatile static int i = 0;

    public static class AddThread extends Thread {
        @Override
        public void run() {
            System.out.println("addThread start");

            Thread2 thread2 = new Thread2();
            System.out.println("thread2 start");
            thread2.start();

            while(++i < 1000000000) {
                if (i == 100000) {
                    try {
                        System.out.println("i == 100000");
                        System.out.println("等待thread2执行完成");
                        thread2.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (i == 100000) {
                    System.out.println("addThread 等待thread2执行完成, 继续完成后面逻辑");
                }

            }
        }
    }

    public static class Thread2 extends Thread {
        @Override
        public void run() {
            int tt = 0;
            System.out.println("开始执行Thread2");
            while(++tt < 100000);
            System.out.println("Thread2执行完成");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AddThread addThread = new AddThread();
        addThread.start();
    }
}

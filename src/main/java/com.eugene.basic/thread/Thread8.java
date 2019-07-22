package com.eugene.basic.thread;

/**
 * 测试线程join和yield方法
 *
 * join方法: 两个api, 一个是无限制等待另一个是有限制等待
 *
 * join方法本质: 让 "当前线程" 等待 调用join的线程先执行
 *
 * 当前线程及是某个线程调用join方法的环境下, 比如在如下case:
 *   在线程addThread的run方法中创建了threads线程并启动了它, 并且thread2调用了它自己的join方法,
 *   所以当前线程指的是addThread线程, 所以addThread线程必须等待thread2线程执行结束后才能干自己
 *   的事。
 *
 * 静态yield()方法: 使当前线程让出cpu的使用权限, 回到就绪队列和其他线程一起争夺cpu的使用权
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

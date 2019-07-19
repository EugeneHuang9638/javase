package com.eugene.basic.thread;

/**
 * 测试Thread api:
 * suspend() => 暂停线程, 但不会释放锁
 * resume() =>
 *
 *
 * sleep, wait, suspend
 * 自动   notify resume
 *
 * 此demo由于t2的resume方法执行在suspend前面, 所以t2被挂起了
 */
public class Thread6 {

    public static Object object = new Object();

    static ChangeObjectThread t1 = new ChangeObjectThread("t1");

    static ChangeObjectThread t2 = new ChangeObjectThread("t2");

    public static class ChangeObjectThread extends Thread {

        public ChangeObjectThread(String name) {
            super.setName(name);
        }

        @Override
        public void run() {
            synchronized(object) {
                System.out.println("in " + getName());
                Thread.currentThread().suspend();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        t1.start();
        Thread.sleep(1000);
        t2.start();

        t1.resume();
        t2.resume();

        t1.join();
        t2.join();
    }

}



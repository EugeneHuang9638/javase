package com.eugene.basic.thread;

/**
 * 测试wait和notify方法
 */
public class Thread5 {

    final static Object object = new Object();

    public static class T1 extends Thread {

        @Override
        public void run() {
            synchronized (object) {
                System.out.println(System.currentTimeMillis() + ": T1 start! ");

                try {
                    System.out.println(System.currentTimeMillis() + ": T1 wait for object");
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(System.currentTimeMillis() + ": T1 end!");
            }
        }
    }

    public static class T2 extends Thread {

        @Override
        public void run() {
            synchronized (object) {
                System.out.println(System.currentTimeMillis() + ": T2 start! notify one thread");

                object.notify();
                System.out.println(System.currentTimeMillis() + ": T2 end!");

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new T1().start();
        new T2().start();
    }
}

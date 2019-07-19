package com.eugene.basic.thread;

/**
 * 测试wait和notify方法
 *
 * wait()和sleep()方法都能让线程等待若干时间，
 * 但wait()需要notify()主动唤醒 sleep()方法在睡眠时间过后自动唤醒
 * wait()方法会释放对象锁, 但sleep()方法不会释放对象锁
 */
public class Thread5 {

    final static Object object = new Object();

    public static class T1 extends Thread {

        @Override
        public void run() {
            // synchronized (object) 表示线程T1要申请object的对象锁, 在synchronized包围的代码块
            // 中执行完成后或者执行了object.wait()方法 该锁会被释放
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
            // 同上, T2线程申请获取object的对象锁
            synchronized (object) {
                System.out.println(System.currentTimeMillis() + ": T2 start! notify one thread");

                // 在执行notify方法时, 并不会将锁释放, 要将synchronized代码块执行完后才释放object的对象锁,
                // 只有释放了object的对象锁后, T1线程才会拿到object的对象所并继续完成后续的逻辑
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
        System.out.println(123);
    }
}

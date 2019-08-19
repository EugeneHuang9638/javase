package com.eugene.basic.thread;

/**
 * 测试Thread的
 * interrupt(中断当前线程)  => 实例方法
 * isInterrupted(判断当前线程是否中断)  =>  实例方法
 * interrupted方法(判断当前线程是否中断，并清除当前中断状态)  =>  类方法
 */
public class Thread3 {

    public static void main(String[] args) {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("current thread");

                    //if (this.isInterrupted()) break;
                }
            }
        };

        thread1.start();
        thread1.interrupt(); // 尽管设置了中断状态, 但是线程并不会停止, 要想将线程停止需要将18行注释去掉
    }
}

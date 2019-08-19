package com.eugene.basic.thread;

/**
 * 测试线程类方法 sleep
 * public static native void sleep(long millis) throws InterruptedException;
 *
 * Sleep方法会抛出InterruptedException，即线程在sleep状态下若被中断了则会抛出该异常
 *
 * 若此时抛出了此异常, 线程也不是被中断状态, 需要在catch异常中再次将线程中断，
 * 可以理解成，外部的中断只是为了触发InterruptedException(实质是在异常catch中又会把
 * 中断的标志给去掉), 只有内部的中断
 * 才是真正的将当前线程对象变成中断状态, 注意中断状态不属于线程状态的枚举state中的一个
 */
public class Thread4 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(){
            @Override
            public void run() {
                while (true) {

                    if (this.isInterrupted()) {
                        System.out.println("interrupted");
                        break;
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        System.out.println("线程在sleep状态下被中断了");
                        System.out.println(this.isInterrupted());
                        Thread.currentThread().interrupt();
                        System.out.println(this.isInterrupted());
                    }

                    Thread.yield();
                }
            }
        };

        thread1.start();
        Thread.sleep(1000);
        thread1.interrupt();
    }
}

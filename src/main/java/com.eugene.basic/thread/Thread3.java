package com.eugene.basic.thread;

/**
 * 测试Thread的
 * interrupt(中断当前线程)  => 实例方法
 * isInterrupted(判断当前线程是否中断)  =>  实例方法
 * interrupted方法(判断当前线程是否中断，并清除当前中断状态)  =>  类方法
 */
public class Thread3 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                while (!this.isInterrupted()) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // 线程因sleep进入waitting状态时，
                        // 当调用线程的interrupt方法，此时会抛出
                        // InterruptedException异常，同时
                        // 又会另外将线程的interrupt标志设置为false
                        System.out.println(this.isInterrupted());
                        e.printStackTrace();

                        // 此时还需要标识线程状态为interrupt才会正常结束循环
                        this.interrupt();
                    }

                    //if (this.isInterrupted()) break;
                }
            }
        };

        thread1.start();
        Thread.sleep(500);
        // 设置了中断状态, 若线程在睡眠，则会抛出InterruptedException异常
        thread1.interrupt();
    }
}

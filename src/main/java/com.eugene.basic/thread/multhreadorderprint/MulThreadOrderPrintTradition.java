package com.eugene.basic.thread.multhreadorderprint;

/**
 * 多个线程按顺序打印(传统法：使用synchronize + await + notify)：
 * 线程A 打印5次
 *
 * 线程B 打印10次
 *
 * 线程C 打印15次
 *
 * 分析流程：
 * 线程A先拿到锁，打印5次后，再通知线程B执行，线程B打印完10后，再通知线程C执行
 *
 * 要实现这么一个逻辑，只有一个线程能拿到锁，且这把锁是公共用的，
 * 前一个线程执行完后，然后再根据条件唤醒对应的线程，
 * 同时还要避免，当前线程拿到了锁，但是目前不是轮到自己执行的情况，此时要释放锁
 *
 * 核心：
 *   同一把锁 + 一个volatile修饰的控制哪个线程顺序执行的字段 + 先notify再wait
 *
 */
public class MulThreadOrderPrintTradition {


    private static Object lock = new Object();
    private static volatile String thread = "A";

    public static void main(String[] args) {
        new Thread(() -> {
            int countA = 5;
            while (true) {
                synchronized (lock) {
                    try {
                        // 因为synchronized的notify是随机的，因此有可能自己拿到了锁，但是当前不是轮到自己执行
                        // 所以要把锁释放掉，并唤醒其他线程
                        if (!"A".equals(thread)) {
                            lock.notifyAll();
                            lock.wait();
                        }

                        System.out.println("线程A打印");
                        if (--countA == 0) {

                            Thread.sleep(2000);
                            countA = 5;
                            thread = "B";
                            lock.notifyAll();
                            lock.wait();
                        }
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(() -> {
            int countB = 10;
            while (true) {
                synchronized (lock) {
                    try {
                        // 因为synchronized的notify是随机的，因此有可能自己拿到了锁，但是当前不是轮到自己执行
                        // 所以要把锁释放掉，并唤醒其他线程
                        if (!"B".equals(thread)) {
                            lock.notifyAll();
                            lock.wait();
                        }

                        System.out.println("线程B打印");
                        if (--countB == 0) {
                            Thread.sleep(2000);
                            countB = 10;
                            thread = "C";
                            lock.notifyAll();
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(() -> {
            int countC = 15;
            while (true) {
                synchronized (lock) {
                    try {
                        // 因为synchronized的notify是随机的，因此有可能自己拿到了锁，但是当前不是轮到自己执行
                        // 所以要把锁释放掉，并唤醒其他线程
                        if (!"C".equals(thread)) {
                            lock.notifyAll();
                            lock.wait();
                        }

                        System.out.println("线程C打印");
                        if (--countC == 0) {
                            Thread.sleep(2000);
                            countC = 10;
                            thread = "A";
                            lock.notifyAll();
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }
}

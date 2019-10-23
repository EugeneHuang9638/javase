package com.eugene.basic.aqs;

import java.util.concurrent.TimeUnit;

/**
 * 测试自定义锁: AQSMySimpleReentryLock
 */
public class AQSDemo01 {

    public static int resource = 45;
    private static AQSMySimpleReentryLock lock = new AQSMySimpleReentryLock();

    public static void testAQSMySelfLock() {
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                while (resource > 0) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        lock.lock();
                        if (resource < 0) {
                            break;
                        }
                        System.out.println(Thread.currentThread().getName() + " 获取锁, resource = " + resource--);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }, "线程" + i).start();
        }

        new Thread(() -> {
            int x = 10;
            while(true && x-- > 0) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }

    public static void testReentryAQSMySelfLock() {
        a();
    }

    public static void a() {
        lock.lock();
        System.out.println("a");
        b();
        lock.unlock();
    }

    public static void b() {
        lock.lock();
        System.out.println("b");
        lock.unlock();
    }

    public static void main(String[] args) {
        testAQSMySelfLock();

        //testReentryAQSMySelfLock();
    }
}

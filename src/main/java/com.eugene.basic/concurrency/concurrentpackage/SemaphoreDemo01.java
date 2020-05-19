package com.eugene.basic.concurrency.concurrentpackage;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 资源有限共享
 *   eg: 停车场案例, 停车场的车位是有限的, 但是车辆超过车位数量了, 所以每次
 *       都只能保证有车位的时候才能放行。
 * 1. 信号量表示可以使用的资源数量
 * 2. 线程申请资源 使用acquire方法
 * 3. 线程占有资源期间若需要释放资源则使用release方法
 */
public class SemaphoreDemo01 {

    public static void main(String[] args) {

        Semaphore semaphore = new Semaphore(5);

        for (int i = 0; i < 10; i++) {
            int index = i;
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("car[" + index + "] 准备进停车场");

                    int time = new Random().nextInt(10);
                    TimeUnit.SECONDS.sleep(time);
                    System.out.println("car[" + index + "] 停车花费了: " + time + "秒" );
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

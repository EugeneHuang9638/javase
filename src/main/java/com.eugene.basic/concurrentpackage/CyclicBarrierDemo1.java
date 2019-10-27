package com.eugene.basic.concurrentpackage;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * 测试循环栅栏:
 *   模拟运动员参加百米冲刺
 *
 *   countDownLatch是为了控制裁判开枪和运动员起跑顺序的
 */
public class CyclicBarrierDemo1 {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    // CyclicBarrier 存在两个构造器, 第二个参数是所有的线程都准备就绪后(await的数量等于构造方法传入的10)执行的线程
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(10, () -> {
        System.out.println("裁判员: 各就各位，预备, 砰！");
        countDownLatch.countDown();
    });

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            int index = i;
            new Thread(() -> {
                try {
                    int count = new Random().nextInt(10);
                    TimeUnit.SECONDS.sleep(count);
                    System.out.println("运动员: " + index + "在起跑线准备就绪。");
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        countDownLatch.await();
        System.out.println("所有运动员开始起跑！");
    }
}

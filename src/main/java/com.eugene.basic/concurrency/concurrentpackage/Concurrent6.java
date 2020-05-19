package com.eugene.basic.concurrency.concurrentpackage;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 测试CyclicBarrier(循环栅栏, CountDownLatch的增强版, 可以重复使用),
 * 
 */
public class Concurrent6 {

    public static class Soldier implements Runnable {

        private String soldier;
        private final CyclicBarrier cyclicBarrier;

        Soldier(CyclicBarrier cyclicBarrier, String soldierName) {
            this.cyclicBarrier = cyclicBarrier;
            this.soldier = soldierName;
        }

        @Override
        public void run() {
            try {
                // 等待所有士兵到齐
                cyclicBarrier.await();
                doWork();
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        private void doWork() {
            try {
                Thread.sleep(Math.abs(new Random().nextInt() % 10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(this.soldier + ": 完成任务！");
        }
    }

    public static class BarrierRun implements Runnable {

        boolean flag;
        int N;

        public BarrierRun(boolean flag, int n) {
            this.flag = flag;
            N = n;
        }

        @Override
        public void run() {
            if (flag) {
                System.out.println("司令: [士兵" + N + "个, 任务完成！");
            } else {
                System.out.println("司令: [士兵" + N + "个, 集合完毕");
                flag = true;
            }
        }
    }

    public static void main(String[] args) {
        final int N = 10;
        Thread[] addSoldier = new Thread[N];
        boolean flag = false;
        CyclicBarrier cyclic = new CyclicBarrier(N, new BarrierRun(flag, N));

        System.out.println("队伍集合！");
        for (int i = 0; i < N; i++) {
            System.out.println("士兵" + i + "报道");
            addSoldier[i] = new Thread(new Soldier(cyclic, "士兵" + i));
            addSoldier[i].start();
        }
    }

}

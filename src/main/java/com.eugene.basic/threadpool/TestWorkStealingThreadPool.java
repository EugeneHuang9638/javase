package com.eugene.basic.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试工作窃取线程池
 *
 *
 *
 */
public class TestWorkStealingThreadPool {

    public static void main(String[] args) throws InterruptedException {
        /**
         * 它默认是根据CPU的核心数来创建线程池中的线程大小
         */
        ExecutorService executorService = Executors.newWorkStealingPool();

        int coreSize = Runtime.getRuntime().availableProcessors();

        System.out.println("当前cpu的核心数：" + coreSize);

        for (int i = 0; i < coreSize + 1; i++) {
            if (i == 0) {
                executorService.execute(new Task(500));
            } else {
                executorService.execute(new Task(1000));
            }
        }

        // 此处一定要睡眠足够的时间，目的是为了让workStealingPool中的线程都执行完
        // 因为workStealingPool线程池中的线程为守护线程
        // 何为守护线程呢？
        // 所谓守护线程就是在后台默默执行的线程,
        // 若主线程不挂或不手动关闭守护线程，那么它就会一直存在
        // 所以这块休眠了3s，可以保证上述创建的线程池中的线程都会被执行完毕了
        Thread.sleep(3000);

    }


    private static class Task implements Runnable {

        int time;
        public Task(int time) {
            this.time = time;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(time);
                System.out.println(time + " **\t**" +Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}

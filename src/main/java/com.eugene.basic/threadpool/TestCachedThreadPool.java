package com.eugene.basic.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试缓存式的线程池
 */
public class TestCachedThreadPool {


    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                System.out.println("我是线程 " + Thread.currentThread().getName());
            });
        }

        // 查看线程池中的线程数量
        System.out.println(executorService);

        // 添加一个任务，看会不会被复用, 并休眠10s钟
        executorService.execute(() -> {
            System.out.println("我是线程 " + Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 休眠60s后，查看是不是只剩下一个线程了
        Thread.sleep(60000);
        System.out.println("休眠达到60s");
        System.out.println(executorService);

        // 运行结果如下
        /**
         * 我是线程 pool-1-thread-1
         * 我是线程 pool-1-thread-2
         * 我是线程 pool-1-thread-3
         * 我是线程 pool-1-thread-4
         * 我是线程 pool-1-thread-5
         * java.util.concurrent.ThreadPoolExecutor@568db2f2[Running, pool size = 5, active threads = 3, queued tasks = 0, completed tasks = 2]
         * 我是线程 pool-1-thread-5
         * java.util.concurrent.ThreadPoolExecutor@568db2f2[Running, pool size = 1, active threads = 0, queued tasks = 0, completed tasks = 6]
         *
         * 一、前面5个打印出来的语句不一定会按照1, 2, 3, 4, 5的顺序
         * 二、第一次打印线程池对象时，发现线程池的数量为5，存活的线程数为3，队列中的任务为0，完成的线程数为2
         *    可以这么理解，完成的线程数是指线程运行完成了，但是还没有进入到存活的线程队列中去
         *
         * 三、打印 "我是线程 pool-1-thread-5" 表示这个任务执行的过程中共用了第五个线程
         * 四、休眠60s打印线程池对象，发现线程池的数量变为1了，同时完成任务的数量变成了6，因此可以证明
         *    每个线程从它执行完任务开始，有一个60s的生命周期，若60过后，此线程就会被回收掉。
         */




    }
}

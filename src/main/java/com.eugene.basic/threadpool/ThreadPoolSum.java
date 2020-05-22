package com.eugene.basic.threadpool;

import java.util.concurrent.*;

/**
 * 使用线程池并行操作，将1加到10000000
 *
 *
 * 分别对比单线程执行和多线程执行的效率
 */
public class ThreadPoolSum {


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        System.out.println("单个线程处理结果");
        Thread t1 = new Thread(() -> {
            Long count = 0L;
            Long start = 1L;
            Long startTime = System.currentTimeMillis();

            while (start <= 10000000) {
                count += start++;
            }
            Long endTime = System.currentTimeMillis();
            System.out.println("耗时： " + (endTime - startTime));
            System.out.println("count = " + count);
        });
        t1.start();
        t1.join();

        // 使用线程池多个线程协作处理10000000平分
        Task task1 = new Task(1L, 2500000L);
        Task task2 = new Task(2500001L, 5000000L);
        Task task3 = new Task(5000001L, 7500000L);
        Task task4 = new Task(7500001L, 10000000L);

        System.out.println("多线程协作处理结果");
        ExecutorService executorService = Executors.newFixedThreadPool(7);
        Long startTime = System.currentTimeMillis();

        Future<Long> submit1 = executorService.submit(task1);
        Future<Long> submit2 = executorService.submit(task2);
        Future<Long> submit3 = executorService.submit(task3);
        Future<Long> submit4 = executorService.submit(task4);

        System.out.println("count = " + (submit1.get()
                + submit2.get()
                + submit3.get()
                + submit4.get()
        ));
        Long endTime = System.currentTimeMillis();

        System.out.println("耗时：" + (endTime - startTime));

        executorService.shutdown();

    }

    private static class Task implements Callable<Long> {
        Long start;
        Long end;

        public Task(Long start, Long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Long call() throws Exception {
            Long count = 0L;
            for (Long i = start; i <= end; i++) count += i;
            return count;
        }
    }


}

package com.eugene.basic.threadpool;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * 计算1到200000的和，使用fork/join实现
 */
public class CountTaskForkJoin extends RecursiveTask<Long> {

    private static final int THRESHOLD = 10000;

    private Long start;

    private Long end;

    public CountTaskForkJoin(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        long sum = 0;
        boolean canCompute = (end - start) < THRESHOLD;
        if (canCompute) {
            // 小于10000的求和，直接使用循环处理
            for (Long i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            // 拆分成100个小任务，设置每个小任务执行的步长
            long step = (start + end) / 100;
            ArrayList<CountTaskForkJoin> subTasks = new ArrayList<>();
            long pos = step;
            for (int i = 0; i < 100; i++) {
                long lastOne = pos + step;
                // 为100个任务拆解出它需要执行的步长，进而构建CountTaskForkJoin实例
                if (lastOne > end) {
                    lastOne = end;
                }

                CountTaskForkJoin countTaskForkJoin = new CountTaskForkJoin(pos, lastOne);
                pos += step + 1;
                subTasks.add(countTaskForkJoin);
                countTaskForkJoin.fork();
            }

            for (CountTaskForkJoin subTask : subTasks) {
                sum += subTask.join();
            }
        }

        return sum;
    }


    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTaskForkJoin countTaskForkJoin = new CountTaskForkJoin(0L, 200000L);
        ForkJoinTask<Long> result = forkJoinPool.submit(countTaskForkJoin);
        try {
            Long res = result.get();
            System.out.println("sum = " + res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

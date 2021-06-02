package com.eugene.basic.parallelsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Resource {

    private volatile static int arr[];

    private final int THREAD_NUM;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    // 默认为-1，表示没有找到
    private static volatile AtomicInteger index = new AtomicInteger(-1);

    public Resource(int[] arr, int threadNum) {
        this.arr = arr;
        this.THREAD_NUM = threadNum;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Resource resource = new Resource(new int[]{19, 23, 44, 2, 7, 37, 97}, 2);
        resource.search(44);

        resource.executorService.shutdown();
    }

    public void search(int target) throws ExecutionException, InterruptedException {
        // 5 / 2 = 2  则表示会切分成0, 1, 2, 3, 4
        int subArrSize = arr.length / THREAD_NUM + 1;
        List<Future<Integer>> searchFutureTask = new ArrayList<>();
        for (int i = 0; i < arr.length; i += subArrSize) {
            int end = i + subArrSize;
            if (end >= arr.length) {
                end = arr.length;
            }

            Future<Integer> submit = executorService.submit(new SearchTask(target, i, end));
            searchFutureTask.add(submit);
        }

        for (Future<Integer> integerFuture : searchFutureTask) {
            Integer integer = integerFuture.get();
            if (integer != -1) {
                System.out.println("target 的下标为: " + integer);
            }
        }
    }

    /**
     * 多线程并行搜索，因此需要指定要搜索的起始位置和结束位置
     * 返回值： 目标元素所在的下标
     */
    public static int doSearch(int target, int beginPos, int endPos) {
        for (int i = beginPos; i < endPos; i++) {
            if (index.get() != -1) {
                return index.get();
            }

            if (arr[i] == target) {
                // 这里对cas操作了一个判断，如果cas失败了，则表示是其他线程先找到了这个元素
                if (!index.compareAndSet(-1, i)) {
                    return index.get();
                }
                return i;
            }

        }

        return -1;
    }

    public static class SearchTask implements Callable<Integer> {

        int begin, end, searchValue;

        public SearchTask(int searchValue, int begin, int end) {
            this.begin = begin;
            this.end = end;
            this.searchValue = searchValue;
        }

        @Override
        public Integer call() throws Exception {
            return doSearch(searchValue, begin, end);
        }
    }

}

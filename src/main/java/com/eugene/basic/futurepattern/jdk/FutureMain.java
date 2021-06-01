package com.eugene.basic.futurepattern.jdk;

import java.util.concurrent.*;

public class FutureMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 构建一个futureTask，它需要一个callable的参数
        FutureTask<String> futureTask = new FutureTask<String>(new RealDataCallable("a"));
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        // 只能使用submit方法来执行futureTask的线程
        executorService.submit(futureTask);

        System.out.println("处理其他业务，等其他业务处理完毕后，再来获取future的值");

        System.out.println("其他业务处理完毕，现在要获取future的值。。若future还么有执行完，调用get方法则会阻塞。。。");

        String result = futureTask.get();
        System.out.println("future执行的结果： result = " + result);

        System.out.println("应用程序可能不会停止，因为线程池里面还有线程。如果要停止应用程序，则需要执行线程池的shutdown方法");
        executorService.shutdown();
    }

}

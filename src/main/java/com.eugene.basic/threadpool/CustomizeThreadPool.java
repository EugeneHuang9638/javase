package com.eugene.basic.threadpool;

import org.omg.CORBA.INTERNAL;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 在使用线程池的submit方法时，
 * 会返回一个future对象，
 * 其中，submit方法接受的参数是一个Callable类型的对象，
 * 所以它支持自定义异常以及线程返回值。
 *
 * 但是这有一个关键点：就是一定要调用future的get方法时才能
 * 获取到返回值，一定要调用future的get方法时，才能捕捉到
 * 线程内部抛出的自定义异常
 */
public class CustomizeThreadPool {

    public static void main(String[] args) {
        // 第一个参数: 核心线程数
        // 第二个参数: 最大线程数
        // 第三个参数: 空闲时间
        // 第四个参数: 空闲时间对应的单位
        // 第五个参数: 使用哪种队列： 使用数组类型的阻塞队列
        // 第六个参数: 创建线程的工厂
        // 第七个参数: 拒绝策略 => AbortPolicy： 拒绝后直接抛出异常
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,
                5,
                3000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
                );

        for (int i = 0; i < 10; i++) {
            Future future = threadPoolExecutor.submit(new Customize());
            try {
                System.out.println("线程运行结果: " + future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        threadPoolExecutor.shutdown();
    }

    static class Customize implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            int randomVal = new Random().nextInt(10);
            if (randomVal > 5) {
                System.out.println(Thread.currentThread().getName() + "抛出异常, 随机数为: " + randomVal);
                throw new IllegalArgumentException("参数异常");
            }

            System.out.printf("休眠5s\n");
            Thread.sleep(5000);
            return randomVal;
        }
    }

}

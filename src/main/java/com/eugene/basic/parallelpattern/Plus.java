package com.eugene.basic.parallelpattern;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 阶段1：计算加法的线程
 */
public class Plus implements Runnable {

    protected static BlockingQueue<Payload> blockingQueue = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        while (true) {
            try {
                // 从阻塞队列中获取一个载体，如果队列为空，则阻塞在这里
                Payload payload = blockingQueue.take();
                // 阶段1算出来结果A
                double plusResult = payload.getParam1() + payload.getParam2();

                // 加法计算完毕，通知乘法的线程
                Payload payload2 = new Payload();
                // 填充阶段1算出来的结果A
                payload2.setParam1(plusResult);
                // 填充阶段2还要用到的参数B
                payload2.setParam2(payload.getParam1());
                Multiply.blockingQueue.put(payload2);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

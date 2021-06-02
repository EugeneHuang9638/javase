package com.eugene.basic.parallelpattern;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Multiply implements Runnable {

    protected static BlockingQueue<Payload> blockingQueue = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        while (true) {
            try {
                // 等待阶段1的线程生产数据
                Payload payload = blockingQueue.take();

                // 执行完乘法结果
                double multiplyResult = payload.getParam1() * payload.getParam2();

                // 通知阶段3的线程
                // 加法计算完毕，通知乘法的线程
                Payload payload2 = new Payload();
                // 填充阶段2算出来的结果D, 阶段3需要用到它
                payload2.setParam1(multiplyResult);
                Div.blockingQueue.put(payload2);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

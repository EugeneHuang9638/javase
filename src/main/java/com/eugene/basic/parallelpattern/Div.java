package com.eugene.basic.parallelpattern;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Div implements Runnable {

    protected static BlockingQueue<Payload> blockingQueue = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        while (true) {
            try {
                Payload payload = blockingQueue.take();
                double result = payload.getParam1() / 2;
                System.out.println("算术运行结果：result = " + result);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

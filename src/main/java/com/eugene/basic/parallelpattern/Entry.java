package com.eugene.basic.parallelpattern;

import java.io.IOException;

public class Entry {

    public static void main(String[] args) throws InterruptedException, IOException {
        // 计算（10 + 4） * 10 / 2

        // 启动阶段1线程
        new Thread(new Plus(), "plus").start();
        // 启动阶段2线程
        new Thread(new Multiply(), "multiply").start();
        // 启动阶段3线程
        new Thread(new Div(), "div").start();

        // 主线程开始将算术表达式丢给阶段1线程，开启流水线操作
        Payload payload = new Payload();
        payload.setParam1(10);
        payload.setParam2(4);
        Plus.blockingQueue.put(payload);
        System.in.read();
    }

}

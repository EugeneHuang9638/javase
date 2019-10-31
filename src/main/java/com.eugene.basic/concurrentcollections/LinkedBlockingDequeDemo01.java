package com.eugene.basic.concurrentcollections;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 测试LinkedBlockingDeque api
 *
 * 阻塞集合, 当队列满了或者集合为null, 则阻塞当前调用方法的队列
 */
public class LinkedBlockingDequeDemo01 {

    public static void main(String[] args) throws Exception {
        LinkedBlockingDeque<Integer> linkedBlockingDeque = new LinkedBlockingDeque(10);

        for (int i = 0; i < 20; i++) {
            int index = i;
            new Thread(() -> {
                try {
                    // 用put方法会出现阻塞功能, add方法不会有这样的功能, 用add方法的话会变成非阻塞功能, 被阻塞的线程无法被执行时会抛出异常
                    linkedBlockingDeque.add(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        System.out.println("主线程执行完毕, 但上述还有10个线程被阻塞中, 可以使用jconsole命令连接进程查看, 里面有10个线程正在被阻塞");
    }
}

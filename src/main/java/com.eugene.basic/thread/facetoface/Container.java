package com.eugene.basic.thread.facetoface;

import java.util.LinkedList;
import java.util.UUID;

/**
 * 写一个同步容器，拥有Put和get方法，以及getCount方法，
 * 能够支持两个生产者线程以及10个消费者线程的阻塞调用
 *
 * ---> 阻塞调用的含义：当消费者调用get方法时，若里面没有值
 * 则阻塞在那里，直到有值后再获取
 */
public class Container<T> {

    private LinkedList<T> linkedList = new LinkedList<>();

    public synchronized void put(T t) {
        this.notifyAll();
        linkedList.addFirst(t);
    }

    /**
     * get这里会出现阻塞调用的情况，
     * 如果长度为0时，则等待生产者往里面生成东西
     * @return
     */
    public synchronized T get() {
        while (this.getCount() == 0) {
            try {
                System.out.println(Thread.currentThread().getName() + "调用get方法时，无消息可拿取，等待中…………");
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return linkedList.pollFirst();
    }

    public synchronized int getCount() {
        return linkedList.size();
    }


    public static void main(String[] args) {
        Container<String> container = new Container<>();

        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                String val = UUID.randomUUID().toString();
                container.put(val);
                System.out.println(Thread.currentThread().getName() + "生产消息：" + val);
            }, "生产者-" + i).start();
        }

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                String val = container.get();

                System.out.println(Thread.currentThread().getName() + "消费消息：" + val);
            }, "消费者-" + i).start();
        }
    }

}

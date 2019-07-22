package com.eugene.basic.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试并发下的HashMap
 * HashMap也是线程不安全的
 */
public class Thread14 {

    static Map<String, String> map = new HashMap<>();

    public static class AddThread implements Runnable {

        int start = 0;

        public AddThread(int start) {
            this.start = start;
        }

        @Override
        public void run() {
            for (int i = start; i < 100000; i+=2 ) {
                map.put(Integer.toString(i), Integer.toBinaryString(i));
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Thread14.AddThread(0));
        Thread t2 = new Thread(new Thread14.AddThread(1));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(map.size());
    }

}

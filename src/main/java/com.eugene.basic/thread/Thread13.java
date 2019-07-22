package com.eugene.basic.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 测试并发下的ArrayList和Vector
 * ArrayList是线程不安全的
 *
 */
public class Thread13 {

    //static List<Integer> all = new Vector();
    static List<Integer> all = new ArrayList();

    public static class AddThread implements Runnable{

        @Override
        public void run() {
            for (int i = 0; i < 1000000; i++) {
                all.add(i);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new AddThread());
        Thread t2 = new Thread(new AddThread());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(all.size());
    }
}

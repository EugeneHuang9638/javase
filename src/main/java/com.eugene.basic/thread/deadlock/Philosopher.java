package com.eugene.basic.thread.deadlock;

import java.util.concurrent.TimeUnit;

/**
 * 哲学家就餐死锁问题demo
 *
 *
 * Philosopher：代表哲学家
 * 默认每个哲学家持有的是左边的筷子
 */
public class Philosopher extends Thread {

    // 哲学家A的id为A，哲学家B的id为B
    private String id;

    // 公共资源：筷子1, 位于哲学家A的左边
    static Object chopstick1 = new Object();

    // 公共资源：筷子2, 位于哲学家B的左边
    static Object chopstick2 = new Object();

    public Philosopher(String id, String name) {
        this.id = id;
        this.setName(name);

    }

    @Override
    public void run() {
        if ("A".equals(id)) {
            // 默认条件: 当前哲学家拿着左手边的筷子。现在想去拿右手边的筷子
            synchronized (chopstick1) {
                // 等待50毫秒，等哲学家B拿到左手边的筷子，再去拿右手边的筷子
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 尝试去拿右手边的筷子
                synchronized (chopstick2) {
                    System.out.println(this.getName() + "拿到右手边的筷子，准备吃饭");
                }

            }
        }

        if ("B".equals(id)) {
            // 默认条件: 当前哲学家拿着左手边的筷子。现在想去拿右手边的筷子
            synchronized (chopstick2) {
                // 等待50毫秒，等哲学家B拿到左手边的筷子，再去拿右手边的筷子
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 尝试去拿右手边的筷子
                synchronized (chopstick1) {
                    System.out.println(this.getName() + "拿到右手边的筷子，准备吃饭");
                }

            }
        }
    }

    public static void main(String[] args) {
        Philosopher philosopherA = new Philosopher("A", "哲学家A");
        philosopherA.start();
        Philosopher philosopherB = new Philosopher("B", "哲学家B");
        philosopherB.start();
    }
}

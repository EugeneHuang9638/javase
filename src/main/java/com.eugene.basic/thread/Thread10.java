package com.eugene.basic.thread;

/**
 * 测试 分门别类的管理: 线程组
 *
 * 将做同样事情的线程放在同一个线程组中
 */
public class Thread10 {

    public static class ThreadGroupName implements Runnable {
        @Override
        public void run() {
            String groupAndName = Thread.currentThread().getThreadGroup().getName() + "-" + Thread.currentThread().getName();

            while(true) {
                System.out.println("I am " + groupAndName);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ThreadGroup tg = new ThreadGroup("PrintGroup");

        Thread t1 = new Thread(tg, new ThreadGroupName(), "T1");
        Thread t2 = new Thread(tg, new ThreadGroupName(), "T2");

        t1.start();
        t2.start();
        System.out.println(tg.activeCount());
        // 此方法可以查看线程组的
        tg.list();
    }

}

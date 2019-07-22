package com.eugene.basic.thread;

/**
 * 测试守护线程:
 *  系统守护者, 比如垃圾回收线程、JIT线程等等
 *  与之对应的还有一个名字叫用户线程, 用户线程的主要作用是完成业务逻辑,
 *  当用户线程全部停止只剩下守护线程后, JVM虚拟机认为工作完成就会退出
 */
public class Thread11 {

    public static class DaemonT extends Thread {

        @Override
        public void run() {
            while (true) {
                System.out.println("I am alive");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    public static void main(String[] args) throws InterruptedException {
        DaemonT t = new DaemonT();
        t.setDaemon(true);
        t.start();

        Thread.sleep(2000);
    }
}

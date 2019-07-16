package com.eugene.basic.thread;

/**
 * 测试线程Stop方法, 导致其他线程读错误数据的问题
 *
 * 几个问题:
 * 1. synchronized 标识临界区读写是否都不允许
 * 2.
 */
public class Thread2 {

    public static void main(String[] args) throws InterruptedException {
        // 开启读进程
        new StopThreadUnsafe.ReadObjectThread().start();

        while (true) {
            Thread t = new StopThreadUnsafe.ChangeObjectThread();
            t.start();
            // 此处休息150毫秒是为了控制台中打出的信息慢些, 因为上面的读进程一直在读
            // 而在ChangeObjectThread进程中要想设置name的值必须要睡上100毫秒, 此时读线程可以拿到user的锁, 并获取它的信息,
            // 所以当调用修改线程的stop方法后, 读线程发现id和name不一致, 所以控制台中输出了信息
            Thread.sleep(150);
            t.stop();
        }
    }
}


class StopThreadUnsafe {
    public static User user = new User();

    public static class User {
        private int id;
        private String name;

        public User() {
            this.id = 0;
            this.name = "0";
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

    }

    /**
     * 设置id, 并休眠100毫秒后再设置name, 最后让出cpu的占有权
     */
    public static class ChangeObjectThread extends Thread {

        @Override
        public void run() {
            while (true) {
                synchronized (user) {
                    int v = (int)(System.currentTimeMillis() / 1000);
                    user.setId(v);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    user.setName(String.valueOf(v));
                }

                Thread.yield();
            }
        }
    }

    public static class ReadObjectThread extends Thread {

        @Override
        public void run() {
            while (true) {
                synchronized (user) {
                    if (user.getId() != Integer.parseInt(user.getName())) {
                        System.out.println(user.toString());
                    }
                }

                Thread.yield();
            }
        }
    }

}

package com.eugene.basic.thread.deadlock;

/**
 * 写一个死锁的程序
 *
 * 死锁的特征：两个及以上的人吃着碗里的想着锅里的
 *
 *
 * 排查死锁情况：
 * 1、执行java自带jps命令： jps -l  ===> 能打印出目前运行的java进程，-l参数可以具体到哪一个启动类
 * 2、jstack java进程ID
 *    由上述第一步可以查看到一些具体的java进程的id，然后传入java自带的jstack命令，即可查看当前进程下
 *    线程的堆栈信息
 *
 * Found one Java-level deadlock:
 * =============================
 * "线程2":
 *   waiting to lock monitor 0x000000001cd6dd38 (object 0x000000076b094b68, a java.lang.String),
 *   which is held by "线程1"
 * "线程1":
 *   waiting to lock monitor 0x000000001cd70728 (object 0x000000076b094ba0, a java.lang.String),
 *   which is held by "线程2"
 *
 * Java stack information for the threads listed above:
 * ===================================================
 * "线程2":
 *         at com.eugene.basic.thread.deadlock.DealLockThread.run(Index.java:77)
 *         - waiting to lock <0x000000076b094b68> (a java.lang.String)
 *         - locked <0x000000076b094ba0> (a java.lang.String)
 *         at java.lang.Thread.run(Thread.java:748)
 * "线程1":
 *         at com.eugene.basic.thread.deadlock.DealLockThread.run(Index.java:77)
 *         - waiting to lock <0x000000076b094ba0> (a java.lang.String)
 *         - locked <0x000000076b094b68> (a java.lang.String)
 *         at java.lang.Thread.run(Thread.java:748)
 *
 * Found 1 deadlock.
 *
 * 从上面的jstack命令打印出来的信息可以知道线程1和线程2死锁了，
 * 线程2在等待锁0x000000076b094b68，并且锁住了0x000000076b094ba0
 * 线程1在等待锁0x000000076b094ba0，并且锁住了0x000000076b094b68
 * 由上可以得出，线程1和线程2死锁了，并且在
 * at com.eugene.basic.thread.deadlock.DealLockThread.run(Index.java:77)
 * 最终去分析这些代码即可。
 *
 * ps：这有个小特点：jstack命令查找出来的信息会带上线程的name，因此我们正确的定义线程的名称，
 * 可以更好方便的定位问题
 *
 */
public class Index {

    public static void main(String[] args) {
        String lockA = "lockA";
        String lockB = "lockB";
        new Thread(new DealLockThread(lockA, lockB), "线程1").start();
        new Thread(new DealLockThread(lockB, lockA), "线程2").start();
    }

}

class DealLockThread implements Runnable {
    private String lockA;
    private String lockB;

    public DealLockThread(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }


    @Override
    public void run() {
        synchronized (lockA) {
            System.out.println(Thread.currentThread().getName() + "线程拿到了lockA，并尝试去拿lockB");

            // 睡两秒，为了更好的呈现死锁效果
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + "线程拿到了lockB，并尝试去拿lockA");
            }
        }
    }
}

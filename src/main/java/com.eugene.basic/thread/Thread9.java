package com.eugene.basic.thread;

/**
 * volatile关键字和JMM(java 内存模型 => 都是围绕原子性、有序性、可见性)
 *
 * volatile关键字作用:
 *   因为多线程的情况出现可能会导出计算机底层使用发出的指令出现顺序上的变动,
 *   volatile关键字就是要告诉虚拟机, 在我修饰的地方处要尤其注意 不能随意改动
 *   指令的顺序。
 *
 * 被volatile修饰的变量:
 *   相当于告诉虚拟机，这个变量可能会被多个线程给修改，为了保证各线程对该变量
 *   的可见性，虚拟机会做一些额外的处理，保证这个变量改动的同时对于所有的线程
 *   都是可见的。
 */
public class Thread9 {

    static volatile int i = 0;

    public static class PlusTask implements Runnable {

        @Override
        public void run() {
            while(i++ < 10000);
        }
    }

    /**
     * 测试volatile关键字无法保证多线程同时操作临界区的原子性
     * 假设volatile关键字能保证多线程同时操作临界区变量的原子性,
     * 那么在如下方法中最终i的输出值应该为10000 * 10 = 100000
     * 但是运行时，很明显它的值=10010远远小于10000 * 10 = 100000
     */
    public static void testVolatileAtomic() throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int m = 0; m < 10; m++) {
            threads[m] = new Thread(new PlusTask());
            threads[m].start();
        }

        for (int n = 0; n < 10; n++) {
            threads[n].join();
        }

        System.out.println(i);
    }


    private static boolean ready;
    private static int number;

    public static class ReaderThread extends Thread {

        @Override
        public void run() {
            while(!ready) {
                number++;
            }

            System.out.println("死循环结束, number = " + number);
        }
    }

    /**
     * 若jvm以client的模式运行此程序时, 由于JIT没有做足够的优化, 导致在主线程中对变量ready和number进行了修改后
     * ReaderThread线程能够 可视 这两个变量 所以会线程很快在死循环中退出
     *   验证当前jdk是否支持client模式可查看jdk的安装目录/jre/bin/下是否有client文件夹 => 默认的64位的jdk时没有client模式的
     *
     * 若以server的模式运行此程序时, Jvm会优化系统, number和ready变量在主线程中的变化时
     * ReaderThread线程都不能看到它的改变，导致循环输出0, 可以添加volatile关键字达到变量可见
     *        同时：还有一种能让人意想不到的方法, 在ReaderThread线程的run方法的死循环中, 添加System.out.print()输出语句,
     *             输出任何信息都行, 你会发现我不添加volatile标识符, 线程也能正常结束！！！！
     *
     *             这是为什么呢？？？
     *
     *             ==>  原因就在于System.out.print()源码中有synchronized关键字, 对于jvm而言, 有synchronized关键字的是需要
     *                  获取和释放对象的锁的, 这些操作对于jvm而言是需要等待时间的, 而此时jvm发现我有闲情了, 那么它就会优化
     *                  系统代码, 尽量保证临界区的变量能够对所有线程可见, 所以线程就正常结束了。
     * @throws InterruptedException
     */
    public static void testNoVisibility() throws InterruptedException {
        new ReaderThread().start();
        Thread.sleep(1000);
        number = 43;
        ready = true;
        System.out.println("主线程休息3秒");
        Thread.sleep(3000);
    }

    public static void main(String[] args) throws InterruptedException {
//        testVolatileAtomic();
        testNoVisibility();
    }

}

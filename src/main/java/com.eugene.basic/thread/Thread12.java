package com.eugene.basic.thread;

/**
 * 测试线程安全, 线程安全是多线程的根基, 不能以牺牲正确性为代价来提高效率
 *
 * 前面咱们知道volatile关键字的作用: 它能保证多个线程对变量的可见性
 * 但是同时咱们也知道 它不能支持符合操作的原子性, 以下代码可以看出:
 *   两个线程同时循环对i累加10000000次进行写入, 但i的值会小于咱们的期望值20000000
 *
 * 当我们将volatile关键字去掉时, 运行个四五遍发现最后输出的i几乎都是20000000, 可能会产生
 * i变量不加volatile关键字也被其他线程可见了, 当然这个咱们要考虑jvm在后面做的事, jvm有闲情
 * 时会尽量将变量对多个变的可见, 加上volatile关键字只不过是强制要求的,不管jvm有没有闲情都要
 * 这么干, 所以当去掉volatile关键字时, 运行的结果也是随机的, 只不过不是总是小于20000000，而
 * 是有几率小于20000000，这种几率就是jvm没有闲情的时候.
 *
 * 若我们要保证最后的结果是外无一失的20000000要如何做呢？
 * 此时可以添加synchronized关键字, 即将26和28行的代码注释去掉
 *
 * synchronized关键字的用法如下:
 *   1. 加锁对象                             -> 执行修饰的代码块时需要获取 指定对象的锁
 *   2. 加锁实例方法(无static修饰的方法)       -> 执行修饰的代码块时需要获取 当前实例的锁
 *   3. 加锁静态方法(static修饰的方法)         -> 执行修饰的代码块时需要获取 当前类的锁
 *
 * 针对如下例子分别使用三种加锁方法完成同步:
 *  1. 加锁对象
 *    synchronized(instance){}
 *  2. 加锁实例方法
 *    public void synchronized increase(){}
 *  3. 加锁静态方法
 *    public static synchronized void increase(){}
 */
public class Thread12 {

    public static class AccountingVol implements Runnable{
        static AccountingVol instance = new AccountingVol();
        static volatile int i = 0;

        public static void increase() {
//            synchronized(instance) {
                i++;
//            }
        }

        @Override
        public void run() {
            for (int j = 0; j < 10000000;j++) {
                increase();
            }
        }

        public static void main(String[] args) throws InterruptedException {
            Thread t1 = new Thread(instance);
            Thread t2 = new Thread(instance);

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            System.out.println(i);
            System.out.println(Integer.MAX_VALUE);
        }
    }
}

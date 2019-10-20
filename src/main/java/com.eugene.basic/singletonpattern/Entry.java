package com.eugene.basic.singletonpattern;

/**
 * 单例模式需要考虑的几个问题:
 *   1. 线程的安全性
 *   2. 性能
 *   3. 懒加载
 */
public class Entry {

    /**
     * 测试饿汉式单例方法.
     * 1. 线程安全性: 无线程安全, 因为实例是在加载类静态属性的时候初始化对象的。 多线程仅仅是获取
     * 2. 性能: 性能一般
     * 3. 懒加载: 不是懒加载, 不是在获取的时候才创建的.
     */
    public static void testEHSSingleton() {
        System.out.println("=============测试饿汉式单例方法开始==============");
        for (int i = 0; i < 20 ; i++) {
            new Thread(() -> {
                System.out.println(EHSSingleton.getInstance());
            }).start();
        }
        System.out.println("=============测试饿汉式单例方法结束==============");
    }

    /**
     * 测试懒汉式单例方法.
     *   1. 是否线程安全: 非线程安全, 因为synchronized关键字是加在代码快中的, 且加锁的代码块中没有二次校验
     *                   实例是否为null, 如果两个线程在同一时刻满足了instance == null的条件,
     *                   此时毕竟会有一个线程拿到锁另外一个线程拿不到, 最终拿到锁的线程实例化对象后
     *                   释放锁, 另外一个线程就会拿到锁并也走实例化对象的逻辑, 此时这两个线程获取的对象就不是
     *                   同一个对象了.
     *   2. 性能: 性能底,
     *   3. 懒加载: 是懒加载
     */
    private volatile static Integer index = 0;
    public static void testLHSSingleton() {
        System.out.println("=============测试懒汉式单例方法开始==============");
        for (int i = 0; i < 20 ; i++) {
            new Thread(() -> {
                System.out.println(LHSSingleton.getInstance().toString());

                if (++index == 19) {
                    // 因为volatile不保证原子性, 所以它有可能不会准确的在最后一行输出
                    System.out.println("=============测试懒汉式单例方法结束==============");
                }
            }).start();
        }
    }

    /**
     * 测试DCL单例模式: 为了解决上述懒汉式单例模式写法的问题.
     *  1. 是否线程安全: 安全的
     *  2. 性能: 比较低
     *  3. 懒加载: 是懒加载
     *
     *  但是有个巨大的问题:
     *    具体看DCLSingleton类
     */
    private volatile static Integer index2 = 0;
    public static void testDCLSingleton() {
        System.out.println("=============测试懒汉式DCL单例方法开始==============");
        for (int i = 0; i < 20 ; i++) {
            new Thread(() -> {
                System.out.println(DCLSingleton.getInstance());

                if (++index2 == 19) {
                    // 因为volatile不保证原子性, 所以它有可能不会准确的在最后一行输出
                    System.out.println("=============测试懒汉式DCL单例方法结束==============");
                }
            }).start();
        }
    }

    /**
     * DCL优化测试
     */
    private volatile static Integer index3 = 0;
    public static void testDCLSingleton2() {
        System.out.println("=============测试懒汉式DCL单例方法开始==============");
        for (int i = 0; i < 20 ; i++) {
            new Thread(() -> {
                System.out.println(DCLSingleton2.getInstance());

                if (++index2 == 19) {
                    // 因为volatile不保证原子性, 所以它有可能不会准确的在最后一行输出
                    System.out.println("=============测试懒汉式DCL单例方法结束==============");
                }
            }).start();
        }
    }

    public static void testHolderSingleton() {
        for (int i = 0; i < 20 ; i++) {
            new Thread(() -> {
                System.out.println(HolderSingleton.getInstance());
            }).start();
        }
    }

    public static void main(String[] args) {
        //testEHSSingleton();
        //testLHSSingleton();
        //testDCLSingleton();
        //testDCLSingleton2();
        //testHolderSingleton();
    }
}

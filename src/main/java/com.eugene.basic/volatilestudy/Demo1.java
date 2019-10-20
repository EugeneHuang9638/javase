package com.eugene.basic.volatilestudy;

import java.util.concurrent.TimeUnit;

/**
 * 根据JMM模型分析volatile关键字
 *
 * JMM模型全称(Java Memory Model)
 *   这个模型是一个概念, 类似于MVC概念,
 *   这个模型表示
 *     对于共享内存中的数据, 每一个线程在工作的时候都会存在自己的工作区间,
 *     线程会在共享内存中copy一份数据到自己的工作区间中去，所以线程间的操作
 *     都是不可见的。 线程要想将数据修改的结果同步到共享内存中的, 则需要对
 *     变量添加volatile关键字, 当对变量进行修改的时候, 该关键字会将变量同
 *     步到共享内存中去，还会对其它线程的工作区间的cache line置为无效。当
 *     其它线程要读取这个变量时，它发现cache line是无效状态, 则会去主内存中
 *     获取(当然如果此时更新变量的线程正在更新时，自己获取的变量可能是旧数据).
 *     以上则是volatile可见性的原理。
 *
 *  可见性: 保证多个线程对主线程的变量是可见的，原理如上。
 *  有序性: 保证被volatile关键字修饰的变量的上下块变量的顺序是不可变的。
 *         背景: 因为在JIT(JVM编译时期), 为了提高效率，在将咱们的代码编译成
 *               CPU可执行的指令时, 会进行指令重排, 它会把cpu认为修改顺序能
 *               提高效率的代码换一下顺序(具体规则没有深入研究过), 使用volatile
 *               关键字修饰变量时, 能保证变量的上部分代码和下部分代码顺序不变，
 *               但"上部分的代码"和"下部分代码"内部变量的顺序可能会进行指令重排。
 *         原理: volatile关键字汇编之后(变成计算机可执行的代码), 会对volatile关
 *               键字修饰的变量添加一把锁, 来保证在指令重排时不影响它的顺序。
 *
 *  volatile关键字使用场景:
 *    1. 状态标识。 (可以按一定的规则控制线程间运行的顺序以及控制线程的停止)
 *    2. DCL(Double Checked Locking): 双重检查锁定
 *        => 为了解决Volatile关键字可见性时其它线程读取这个变量时的不确定性.
 *           也就是如果不添加，当线程A对变量修改时, 虽然将其它线程跟这个变量
 *           的cache line置为无效了, 当其它线程此时从主存中获取数据时有可能
 *           线程A正在更新，所以其它线程获取到的数据就是旧数据.
 *           DCL就是为了解决这个问题
 *
 *                              volatile和synchronized的区别
 *                         volatile                         synchronized
 *  使用上的区别            只能修饰变量                         可以修饰代码块、实例方法、静态方法
 *  有序性                  能保证                             也能保证, 但是代价太大, eg: synchronized中嵌套synchronized
 *  原子性                  不能保证                            能保证
 *  可见性                  能保证                              能保证
 *  是否引起阻塞             不会                                会
 *
 *
 */
public class Demo1 {

    private static int initValue = 1;
    //private static volatile int initValue = 1;

    // 第8行注释，第7行解开  =>  程序死循环, 在第一个线程中死循环了, 因为initValue在线程2中被修改了, 但是也仅仅是在线程2的工作区间可见
    // 第7行注释，第8行解开  =>  程序等待5秒钟后, 输出Exit loop, initValue = 5  也就是在线程2对initValue修改对线程1可见了
                              //等待5秒中的原因是: 线程2每过一秒对initValue加1


    private final static int MAX_VALUE = 5;

    public static void main(String[] args) {
        new Thread(() -> {
            while(initValue < MAX_VALUE) {
                if (initValue != MAX_VALUE) {
                    System.out.printf("Reader thread: initValue[" + initValue + "]\n");
                }
            }

            System.out.println("Exit loop, initValue = " + initValue);
        }, "Reader thread").start();

        new Thread(() -> {
            int localValue = initValue;
            while(localValue < MAX_VALUE) {
                initValue = ++localValue;
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Writer thread").start();
    }
}

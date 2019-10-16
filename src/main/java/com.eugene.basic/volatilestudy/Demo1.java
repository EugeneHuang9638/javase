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
 *     变量添加volatile关键字, 该关键字除了将变量同步到共享内存中去外，还会
 *     对其它内存工作区间的这个变量的cache line置为无效, 保证其它线程取这个
 *     变量的时候会再去共享内存中copy一份到自己的工作区间。
 *     以上则是volatile可见性的原理。
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
                //System.out.printf("Reader thread: initValue[" + localValue + "]\n");
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

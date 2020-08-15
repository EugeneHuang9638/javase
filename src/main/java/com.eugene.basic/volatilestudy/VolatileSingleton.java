package com.eugene.basic.volatilestudy;

/**
 * Volatile保证单例模式下的安全性
 */
public class VolatileSingleton {


    /**
     * 要添加volatile关键字修饰，原因如下：
     * 创建对象大概需要3个操作：
     * 1、在创建对象时，jvm会在内存中申请一个内存空间
     * 2、创建对象的过程中有一系列操作(位于jvm中)
     * 3、将instance指向刚刚创建出来的对象
     *
     * 因为上面的3个步骤无数据依赖情况，因此有可能出现指令重排的情况，所以最好添加volatile关键字来
     * 禁止此变脸的指令重排
     *
     */
    public volatile static VolatileSingleton instance = null;


    private VolatileSingleton() {
        System.out.println("调用了构造方法");
    }

    public static VolatileSingleton getInstance() {
        if (instance == null) {
            synchronized (VolatileSingleton.class) {
                if (instance == null) {
                    instance = new VolatileSingleton();
                }
            }
        }

        return instance;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                VolatileSingleton.getInstance();
            }).start();
        }
    }

}

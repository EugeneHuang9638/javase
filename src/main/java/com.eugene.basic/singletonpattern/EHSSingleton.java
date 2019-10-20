package com.eugene.basic.singletonpattern;

/**
 * 饿汉式单例模式
 *
 * 获取对象实例入口直接返回对象, 对象先创建
 */
public class EHSSingleton {

    /**
     * 静态的, 保证能在主内存中对于线程共享
     */
    private static EHSSingleton ehsSingleton;

    static {
        ehsSingleton = new EHSSingleton();
    }

    /**
     * 设置为私有的, 保证外部不能new对象
     */
    private EHSSingleton() {}

    public static EHSSingleton getInstance() {
        return ehsSingleton;
    }

}

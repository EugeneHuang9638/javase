package com.eugene.basic.singletonpattern;

/**
 * 测试懒汉式单例模式(获取的时候才创建)
 *   1. 线程安全性
 *   2. 性能
 *   3. 懒加载
 */
public class LHSSingleton {

    private static LHSSingleton lhsSingleton;

    private LHSSingleton() {}

    public static LHSSingleton getInstance() {
        if (lhsSingleton == null) {
            synchronized(LHSSingleton.class) {
                lhsSingleton = new LHSSingleton();
            }
        }

        return lhsSingleton;
    }
}

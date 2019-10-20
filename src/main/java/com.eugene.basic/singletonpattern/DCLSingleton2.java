package com.eugene.basic.singletonpattern;

import java.net.Socket;

/**
 * DCL单例模式优化版, 解决指令重排导致的空指针异常
 */
public class DCLSingleton2 {

    // 拥有一个引用属性, 当有多个线程同时调用getInstance方法时, 有可能在某个线程获取的实例中的socket属性为null.
    // 此时若也调用socket的一些方法, 就会出现空指针异常. 出现这个原因就是因为Happens-Before原则(CPU指令重排),
    // 重排后有可能对socket代码放在了实例化代码的后面.
    // 也就是在initInstance方法中socket和dclSingleton初始化顺序调换了。 并且在先实例化DCLSingleton时, 另一个
    // 线程就return了, 并且执行了socket的部分方法, 造成控制针异常, 解决方案就是对private static DCLSingleton dclSingleton;
    // 添加volatile关键字, 使用它的有序性保证socket在它前面初始化。
    private static Socket socket;
    private volatile static DCLSingleton2 dclSingleton;

    private DCLSingleton2() {
    }

    public static DCLSingleton2 getInstance() {
        if (dclSingleton == null) {
            synchronized (DCLSingleton2.class) {
                if (dclSingleton == null) {
                    dclSingleton = initInstance();
                }
            }
        }

        return dclSingleton;
    }

    public static DCLSingleton2 initInstance() {
        socket = new Socket();
        dclSingleton = new DCLSingleton2();
        return dclSingleton;
    }

}

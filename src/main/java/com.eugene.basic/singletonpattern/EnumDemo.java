package com.eugene.basic.singletonpattern;

/**
 * 测试枚举特性: 常量、只初始化一次(类加载的时候)
 */
public enum EnumDemo {

    // A, B, C, D都是EnumDemo类型
    A, B, C, D;

    public static void testA() {
        for (int i = 0; i < 10; i++) {
            System.out.println(A);
        }
    }

    public static void main(String[] args) {
        B.testA();
    }
}

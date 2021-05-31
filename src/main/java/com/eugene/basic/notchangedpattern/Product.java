package com.eugene.basic.notchangedpattern;

/**
 * 不可变模式
 * 1、类添加final修饰符，防止子类继承
 * 2、字段添加final修饰符，保证属性只能赋值一次
 * 3、存在一个创建完整对象的构造方法（在构造方法中为final对象赋值）
 * 4、去除所有属性的setter方法
 */
public final class Product {

    private final String no;

    private final String name;

    public Product(String no, String name) {
        this.no = no;
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public String getName() {
        return name;
    }
}

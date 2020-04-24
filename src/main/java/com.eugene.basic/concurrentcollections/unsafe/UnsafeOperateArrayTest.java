package com.eugene.basic.concurrentcollections.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeOperateArrayTest {

    private static String []arr = new String[] {"a", "b", "c", "d"};

    private static Unsafe unsafe;

    private static int ns;

    private static int base;

    static {
        unsafe = getUnsafe();
        // 数组中存储的对象的对象头大小， 数组类型，默认为4
        ns = unsafe.arrayIndexScale(Object[].class);
        // 数组中第一个元素的起始位置, 数组类型，默认为16
        base = unsafe.arrayBaseOffset(String[].class);
    }

    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println(ns);
        // 第一个参数为获取源, 在这里获取源是一个字符串的数组
        // 第二个参数为基于base(第一个元素)而言，获取后面的第几个  --->  获取第四个元素
        System.out.println(unsafe.getObject(arr, base + 3 * ns));

        // ================================
        // int类型的1的二进制为:
        // 00000000 00000000 00000000 00000001
        // 1前面的高位有31个0

        // int类型的3的二进制为:
        // 00000000 00000000 00000000 00000011
        // 3的前面的高位又30个0
        // 所以Integer.numberOfLeadingZeros(x)  ===> 就是获取x前面的高位有多少个0
        System.out.println(Integer.numberOfLeadingZeros(1));
        System.out.println(Integer.numberOfLeadingZeros(4));


        /**
         *
         * Unsafe unsafe = getUnsafe();
         * // 数组中存储的对象的对象头大小， 数组类型，默认为4
         * ns = unsafe.arrayIndexScale(Object[].class);
         * // 数组中第一个元素的起始位置, 数组类型，默认为16
         * base = unsafe.arrayBaseOffset(String[].class);
         * unsafe.getObject(arr, base + 3 * ns)  ==> 获取的是数组中下标为4的元素
         *
         *
         * int SBASE = UNSAFE.arrayBaseOffset(Segment[].class);
         *
         * SBASE + (j << (31 - Integer.numberOfLeadingZeros(UNSAFE.arrayIndexScale(Segment[].class))))
         *
         * UNSAFE.arrayIndexScale(Segment[].class)获取的是对象类型，所以返回的值默认为4
         * 而4的二进制为 00000000 00000000 00000000 00000100
         * 所以Integer.numberOfLeadingZeros(4) = 29
         * SBASE + (j << (31 - 29)) = SBASE + j * 2的平方 = SBASE + j * 4
         *
         * 所以找到的是第五个位置
         */

    }

}

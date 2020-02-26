package com.eugene.basic.jdk1dot8stream;

public class Test {

    public static SumHandler sum1(final int x, final int y) {
        return (z, h) ->
            z + h;
    }

    public static SumHandler sum2(final int x, final int y) {
        return (z, h) ->
                x + y;
    }

    public static void main(String[] args) {
        // 实际上是返回了一个SumHandler对象，它里面有一个sum方法
        System.out.println(sum1(1, 2));

        // 实际上是调用返回的SumHandler对象的sum方法。打印输出9
        System.out.println(sum1(1, 2).sum(4, 5));


        // 实际上是调用返回的SumHandler对象的sum方法。打印输出3
        System.out.println(sum2(1, 2).sum(4, 5));
    }
}

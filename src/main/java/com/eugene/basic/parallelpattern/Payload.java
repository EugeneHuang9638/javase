package com.eugene.basic.parallelpattern;

/**
 * 各阶段的线程交互的载体
 */
public class Payload {

    // 需要做运算的参与者a
    private double param1;

    // 需要做运算的参与者b
    private double param2;


    public double getParam1() {
        return param1;
    }

    public void setParam1(double param1) {
        this.param1 = param1;
    }

    public double getParam2() {
        return param2;
    }

    public void setParam2(double param2) {
        this.param2 = param2;
    }
}

package com.eugene.basic.futurepattern.customer;

/**
 * RealData是最终需要使用的数据模型。它的构造很慢，
 * 用sleep函数模拟这个过程。
 */
public class RealData implements Data {

    protected final String result;

    public RealData(String result) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.result = result;
    }

    @Override
    public String getResult() {
        return this.result;
    }
}

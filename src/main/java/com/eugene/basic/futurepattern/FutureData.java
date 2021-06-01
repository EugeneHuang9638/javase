package com.eugene.basic.futurepattern;

/**
 * FutureData实现了一个快速返回的RealData包装。它只是一个包装，或者说是一个RealData的虚拟实现。
 * 因此，他可以很快被构造并返回。当使用FutureData的getResult方法时，如果实际的数据灭有准备好，那么
 * 程序就会阻塞，等RealData准备好并注入FutureData中才最终返回数据
 *
 *
 * FutureData是future模式的关键。它实际上是真实数据的代理，封装了获取RealData的等待过程
 *
 */
public class FutureData implements Data {

    protected volatile RealData realData = null;

    protected volatile boolean isReady = false;

    public synchronized void setRealData(RealData realData) {
        if (isReady) {
            return;
        }

        this.realData = realData;
        isReady = true;
        // realData已经被注入，唤醒getResult方法
        this.notifyAll();
    }

    @Override
    public synchronized String getResult() {
        /**
         * 思考一下： 为什么这里要引入一个isReady变量？我们直接让getResult方法等待不就行了吗？
         *
         * 这里是为了防止realData的构建很快，setReadData方法内的通知已经完了，才触发getResult方法，
         * 这就会导致此方法阻塞，在这种情况下是不需要阻塞的。因此，这里需要引入isReady字段
         */
        while (!isReady) {
            System.out.println("getResult wait");
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return realData.getResult();
    }
}

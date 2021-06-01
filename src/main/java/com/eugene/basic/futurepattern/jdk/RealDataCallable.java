package com.eugene.basic.futurepattern.jdk;

import java.util.concurrent.Callable;

/**
 * Callable接口只有一个方法call，它会返回需要构造的实际数据。
 * 这个Callable接口也是Future框架和应用程序之间的重要接口要实现自己的
 * 业务系统，通常需要实现自己的Callable对象。此外，FutureTask类与应用紧密相关，通常可以
 * 使用Callable实例构造一个FutureTask实例，并将它提交给线程池
 *
 */
public class RealDataCallable implements Callable<String> {

    private String para;

    public RealDataCallable(String para) {
        this.para = para;
    }

    @Override
    public String call() throws Exception {
        // 构建真实数据，使用sleep模拟
        Thread.sleep(2000);

        return para;
    }
}

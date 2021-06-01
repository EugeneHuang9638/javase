package com.eugene.basic.futurepattern.customer;

/**
 * 客户端程序：client主要实现了获取FutureData，开启构造ReadData的线程功能。
 * 并在接受请求后，很快返回FutureData。
 *
 * 注意：它不会等待数据挣得构造完毕在返回，而是立即返回FutureData，即使这个时候FutureData内并没有真实数据
 */
public class Client {

    public Data request(final String queryStr) {
        final FutureData futureData = new FutureData();
        new Thread(() -> {
            // 构造的很慢, 单据在另外一个线程构建
            RealData realData = new RealData(queryStr);
            // 待realData构建完毕后，再填充到futureData中
            futureData.setRealData(realData);
        }).start();

        return futureData;
    }

}

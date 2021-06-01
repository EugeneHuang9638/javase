package com.eugene.basic.futurepattern;

/**
 * main函数，它主要负责调用Client发起请求，并消费返回的数据
 */
public class Main {

    public static void main(String[] args) {
        Client client = new Client();
        FutureData futureData = (FutureData) client.request("Hello, avengerEug!");

        /**
         * futureData的getResult方法内部可能是阻塞的，
         * 只有当内部的realData数据被填充进去了，这个方法才会被解除阻塞
         */
        System.out.println(futureData.getResult());

    }

}

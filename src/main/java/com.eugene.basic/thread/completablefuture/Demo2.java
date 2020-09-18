package com.eugene.basic.thread.completablefuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 以电商的商品详情页为demo，测试CompletableFuture功能
 *
 * 在电商项目中，要获取一个商品详情，有如下步骤
 * 前置条件：前端传入skuId
 * 1、根据skuId获取Sku实体
 * 2、获取sku的图片信息  --> 不依赖第一步
 * 3、根据sku实体的spuId，来填充spu的基础属性
 * 4、根据sku实体的spuId，获取spu的商品介绍信息(大图)
 * 5、根据sku实体的spuId，获取sku销售属性
 *
 * 上述五步而言，其中3、4、5步依赖于第一步，
 * 而2、3、4、5无任务顺序
 * 因此，我们可以等第一步执行完后，在异步执行2、3、4、5步
 */
public class Demo2 {

    public static void main(String[] args) {
        String skuId = "54467132467410126";
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                20,
                30,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        // 1、根据skuId获取Sku实体
        CompletableFuture<Map<String, Object>> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("根据skuId获取Sku实体");
            Map<String, Object> map = new HashMap<>();
            map.put("spuId", 123);
            return map;
        }, executor);

        // 2、获取sku的图片信息  --> 不依赖第一步
        CompletableFuture<Void> skuImageFuture = CompletableFuture.runAsync(() -> {
            System.out.println("根据skuId: 【 " + skuId + " 】 获取spu基础属性");
        }, executor);

        // 3、根据sku实体的spuId，来填充spu的基础属性
        CompletableFuture<Void> spuBaseAttrFuture = skuInfoFuture.thenAcceptAsync((resp) -> {
            System.out.println("根据spuId: 【 " + resp.get("spuId") + " 】 获取spu基础属性");
        }, executor);

        // 4、根据sku实体的spuId，获取spu的商品介绍信息(大图)
        CompletableFuture<Void> spuBigImageFuture = skuInfoFuture.thenAcceptAsync((resp) -> {
            System.out.println("根据spuId: 【 " + resp.get("spuId") + " 】 获取spu商品介绍(大图)");
        }, executor);

        // 5、根据sku实体的spuId，获取sku销售属性
        CompletableFuture<Void> skuSaleAttrFuture = skuInfoFuture.thenAcceptAsync((resp) -> {
            System.out.println("根据spuId: 【 " + resp.get("spuId") + " 】 获取sku销售属性");
        }, executor);

        // 等待上述的线程走执行完后，再接着往下执行
        CompletableFuture.allOf(skuImageFuture, spuBaseAttrFuture, spuBigImageFuture, skuSaleAttrFuture);
        executor.shutdown();
        System.out.println("查找出商品的详细信息，返回商品详情信息");
    }
}

package com.eugene.basic.concurrency.concurrentpackage;

import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 类似于计算器, 能够控制线程按照顺序执行
 *   eg: 某个线程A必须要等待前面4个线程执行完了才开始执行
 *   底层原理是通过计数器来实现(加了volatile)
 *
 *
 * 实现第三方服务内部线程运行流程
 * eg: 假设有一个第三方服务提供一些厂商库存信息, 但是需要跟不同的厂商打交道, 最后将数据全部获取并整理后返回给调用者
 *     调用者   ---------->    第三方服务内部与其它厂商拿取信息    ------>  第三方线程内部获取信息并整理后返回调用者
 *              第三方服务          内部是多线程异步执行
 *
 * 如下: 第三方服务需要获取用户搜索的滑板鞋关键字, 然后帮安踏、特步、耐克、阿迪达斯等品牌卖, 所以可以跟他们的后台进行
 *      交互(获取指定类型鞋子的库存), 最终整理后返回滑板鞋库存总数量
 */
public class CountDownLatchDemo1 {

    private static CountDownLatch countDownLatch = new CountDownLatch(4);
    private static final String[] brands = {"安踏", "特步", "耐克", "阿迪达斯"};
    private static List<String> totalMount = new Vector();

    public static void main(String[] args) throws Exception {
        final String requirement = "滑板鞋";
        for (int i = 0; i < brands.length; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    int mount = new Random().nextInt(10);
                    TimeUnit.SECONDS.sleep(mount);
                    System.out.println(Thread.currentThread().getName() + "剩余" + requirement + "数量: " + mount);
                    totalMount.add(brands[index] + requirement + "库存: " + mount);
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, brands[i] + "厂商").start();
        }

        // 主线程阻塞到这儿, 等CoutDownLatch计数器为0时会继续执行
        countDownLatch.await();
        System.out.println("==================获取成功==================");
        totalMount.forEach(obj -> {
            System.out.println(obj);
        });

    }
}
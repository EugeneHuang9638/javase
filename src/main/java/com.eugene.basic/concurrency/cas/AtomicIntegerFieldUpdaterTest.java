package com.eugene.basic.concurrency.cas;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * 测试AtomicIntegerFieldUpdater，将指定字段标注为线程安全的修改。
 *
 * 其案例场景为：
 * 假设某地要进行一场选举。如果选民投了候选人一票，就+1，否则记为0。
 */
public class AtomicIntegerFieldUpdaterTest {

    public static class Candidate {
        int id;
        private int score;
    }

    // 假设为10000个人参与
    public static int allJoiner = 10000;

    // 为某个字段添加cas功能
    public final static AtomicIntegerFieldUpdater<Candidate> scoreUpdater = AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");

    // 额外添加一个atomicInteger字段，用来验证scoreUpdater是否被安全的添加
    public final static AtomicInteger allScore = new AtomicInteger(0);

    static CountDownLatch countDownLatch = new CountDownLatch(allJoiner);

    public static void main(String[] args) throws InterruptedException {
        // 10000个人投选avengerEug
        final Candidate avengerEug = new Candidate();
        // 模拟10000个人随机投票，当随机数大于0.4，则当成赞成票
        for (int i = 0; i < allJoiner; i++) {
            new Thread(() -> {
                if (Math.random() > 0.4) {
                    // 使用AtomicIntegerFieldUpdater来对score字段进行写入
                    scoreUpdater.incrementAndGet(avengerEug);

                    // 额外写入allScore字段，用来最后面的验证
                    allScore.incrementAndGet();
                }

                countDownLatch.countDown();
            }).start();
        }

        // 等待所有线程投票完成
        countDownLatch.await();
        System.out.println("所有人投票结束：allScore = " + allScore.get() + ", avengerEug的赞同票为score = " + scoreUpdater.get(avengerEug));

    }

}

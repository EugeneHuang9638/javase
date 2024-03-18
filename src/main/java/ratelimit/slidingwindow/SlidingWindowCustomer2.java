package ratelimit.slidingwindow;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 自己来实现滑动窗口
 * @author muyang
 * @create 2024/3/18 20:08
 */
public class SlidingWindowCustomer2 {

    /**
     * 用于存储窗口中的值
     */
    private Queue<Long> windowStore;

    /**
     * 拆分多少个小时间窗口
     */
    private Integer windowsSize;

    /**
     * 窗口中最多能存储的元素
     */
    private Integer limit;

    public SlidingWindowCustomer2(Integer windowsSize, Integer limit) {
        this.windowStore = new LinkedBlockingQueue<>();
        this.windowsSize = windowsSize;
        this.limit = limit;
    }

    /**
     * 看当前请求是否能被处理
     */
    public synchronized boolean tryAcquire() {
        // 1. 获取当前时间
        Long now = System.currentTimeMillis();
        // 2. 获取当前窗口的最小时间
        Long startTimestamp = now - windowsSize;
        // 3. 遍历窗口中的元素，如果元素 比 earliestTimestamp 小，这认为已经不在窗口下，需要移除
        while (!windowStore.isEmpty() && windowStore.peek() < startTimestamp) {
            // peek 取第一个元素， pool取第一个并移除第一个元素
            Long poll = windowStore.poll();
            System.out.println("当前时间窗口. startTimestamp: " + startTimestamp + ", endTimeStamp: " + now + "。 移除元素：" + poll);
        }

        // 4. 判断当前窗口的数量是否已经超过限制
        if (windowStore.size() != 0 && windowStore.size() >= limit) {
            // 满了，不处理
            return false;
        }

        // 5. 没有满，直接放进去
        return windowStore.offer(now);
    }

    public static void main(String[] args) throws InterruptedException {
        // 使用1000毫秒的窗口（1秒），以承载每秒最多100个请求（QPS）
        SlidingWindowCustomer2 slidingWindowCustomer2 = new SlidingWindowCustomer2(1000, 100);

        // 模拟30秒的请求发送
        long simulationDuration = 30000; // 30秒
        long simulationStart = System.currentTimeMillis();
        long requestId = 0;

        while (System.currentTimeMillis() - simulationStart < simulationDuration) {
            boolean allowed = slidingWindowCustomer2.tryAcquire();
            if (allowed) {
                System.out.println("通过 - 请求ID: " + ++requestId);
            } else {
                System.out.println("拒绝 - 请求ID: " + ++requestId);
            }

            // 模拟请求间隔为10毫秒
            Thread.sleep(10);
        }

        System.out.println("模拟结束");
    }
}

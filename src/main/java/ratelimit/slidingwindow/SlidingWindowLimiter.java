package ratelimit.slidingwindow;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用Queue
 * @create 2024/1/11 10:03
 */
public class SlidingWindowLimiter {

    /**
     * 如果是5s限制请求1000个请求，则windowSizeInMillis为5000，limit为1000
     * 表示会有5000个小窗口，来接收1000个请求
     */
    private final long windowSizeInMillis;
    private final int limit;
    private final Queue<Long> requests;
    private final ReentrantLock lock;

    public SlidingWindowLimiter(long windowSizeInMillis, int limit) {
        this.windowSizeInMillis = windowSizeInMillis;
        this.limit = limit;
        this.requests = new LinkedList<>();
        this.lock = new ReentrantLock();
    }

    public boolean isAllowed() {
        /**
         * 如果不加锁，则会出现操作queue的线程安全问题。
         */
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();

            // 移除窗口之前的所有请求时间戳
            while (!requests.isEmpty() && currentTime - requests.peek() > windowSizeInMillis) {
                requests.poll();
            }

            // 检查当前窗口内请求数量是否超过最大允许值
            if (requests.size() < limit) {
                // 如果没有超过，记录下当前请求的时间戳，并允许这个请求
                requests.offer(currentTime);
                return true;
            }

            // 如果超过了最大允许值，拒绝这个请求
            return false;
        } finally {
            lock.unlock();
        }
    }

}

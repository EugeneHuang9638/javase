package ratelimit;

/**
 * 限流算法
 * @create 2024/1/10 19:58
 */
public interface RateLimiter {

    boolean isAllow(String resource);

}

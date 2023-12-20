package taolu.filter;

import taolu.filter.chain.FilterChain;

import java.util.Random;

/**
 */
public class Test2Filter implements Filter {

    @Override
    public void doFilter(HttpRequest request, FilterChain filterChain) {
        System.out.println("test2Filter before");

        // 如果上一个filter
        int x = 2;
        int random = new Random().nextInt(10);
        if (random / x == 0) {
            // 如果随机出来的是2的整数，认为业务执行失败，则不继续往下执行
            return;
        }

        System.out.println("test2Filter after");
    }
}

package taolu.filter;

import taolu.filter.chain.FilterChain;
import taolu.filter.chain.StandardFilterChain;

/**
 *
 * filter特点：
 * beforeA -> logicA -> beforeB -> logicB -> afterB -> afterA
 * 整个链路就是串行的。如果在beforeB中验证不通过，那后面的logicB, afterB, afterA就都不会执行了。
 * 与pipeChain相比：就是filter之间会互相影响，而pipe不会互相影响。
 * filterChain适用场景：servlet的filter（比如登录过滤器）
 *
 *
 *
 * @author muyang
 * @create 2023/12/20 20:22
 */
public class Entry {

    public static void main(String[] args) {
        FilterChain filterChain = new StandardFilterChain();
        filterChain.addFilter(new Test1Filter());
        filterChain.addFilter(new Test2Filter());

        // 开始执行filter
        filterChain.doFilter(new StandardHttpRequest());
    }

}

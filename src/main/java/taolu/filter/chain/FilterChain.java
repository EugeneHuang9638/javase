package taolu.filter.chain;

import taolu.filter.Filter;
import taolu.filter.HttpRequest;

/**
 */
public interface FilterChain {

    /**
     * 开始执行链
     * @param httpRequest
     */
    void doFilter(HttpRequest httpRequest);

    /**
     * 添加过滤器
     * @param filter
     */
    void addFilter(Filter filter);

}

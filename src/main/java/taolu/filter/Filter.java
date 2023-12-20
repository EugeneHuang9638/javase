package taolu.filter;

import taolu.filter.chain.FilterChain;

/**
 */
public interface Filter {

    /**
     * 当前执行的filter
     * @param request
     * @param filterChain 当前chain
     */
    void doFilter(HttpRequest request, FilterChain filterChain);


}

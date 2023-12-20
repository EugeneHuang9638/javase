package taolu.filter.chain;

import taolu.filter.Filter;
import taolu.filter.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class StandardFilterChain implements FilterChain {

    private List<Filter> filterList = new ArrayList<Filter>();

    private int currentIndex = 0;

    @Override
    public void doFilter(HttpRequest httpRequest) {
        if (currentIndex == filterList.size()) {
            return;
        }

        Filter filter = filterList.get(currentIndex);

        currentIndex = currentIndex + 1;

        filter.doFilter(httpRequest, this);
    }

    @Override
    public void addFilter(Filter filter) {
        if (filterList.contains(filter)) {
            return;
        }

        filterList.add(filter);
    }
}

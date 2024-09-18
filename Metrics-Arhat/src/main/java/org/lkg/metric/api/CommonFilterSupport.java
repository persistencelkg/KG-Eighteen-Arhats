package org.lkg.metric.api;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * Description: 自定义servlet filter强化类，为后续过滤器能力提供同一个处理，
 * 本质 基于 FilterRegistrationBean 定制化 filter 执行 & 控制执行顺序
 * Author: 李开广
 * Date: 2024/9/5 4:55 PM
 */
public class CommonFilterSupport implements Filter {

    private static final LinkedList<CommonFilter> linkedHashSet = new LinkedList<>();

    public CommonFilterSupport(Iterator<CommonFilter> commonFilterIterator) {
        commonFilterIterator.forEachRemaining(this::addCommonFilter);
        linkedHashSet.sort(Comparator.comparing(CommonFilter::getOrder));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        new CommonChain((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain).proceed();
    }

    public void addCommonFilter(CommonFilter commonFilter) {
        linkedHashSet.add(commonFilter);
    }


    static class CommonChain implements CommonFilter.SelfChain {

        private final long startTime;
        private final HttpServletRequest httpServletRequest;
        private final HttpServletResponse httpServletResponse;
        private final FilterChain filterChain;

        private final Iterator<CommonFilter> commonFilters;

        public CommonChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
            this.startTime = System.currentTimeMillis();
            this.httpServletRequest = request;
            this.httpServletResponse = response;
            this.filterChain = chain;
            commonFilters = linkedHashSet.iterator();
        }

        @Override
        public HttpServletRequest request() {
            return httpServletRequest;
        }

        @Override
        public HttpServletResponse response() {
            return httpServletResponse;
        }

        @Override
        public void proceed() throws ServletException, IOException {
            if (commonFilters.hasNext()) {
                commonFilters.next().filter(this);
            } else {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
        }

        @Override
        public Duration duration() {
            return Duration.ofMillis(System.currentTimeMillis() - startTime);
        }
    }
}

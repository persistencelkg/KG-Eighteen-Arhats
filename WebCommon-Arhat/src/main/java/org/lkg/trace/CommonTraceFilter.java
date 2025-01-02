package org.lkg.trace;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lkg.constant.LinkKeyConst;
import org.lkg.core.*;
import org.lkg.metric.api.CommonFilter;
import org.lkg.simple.ObjectUtil;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/25 8:50 PM
 */
@AllArgsConstructor
@Slf4j
public class CommonTraceFilter implements CommonFilter {

    private final TraceHolder traceHolder;
    private final FullLinkPropagation.Getter<HttpServletRequest, String> GETTER = HttpServletRequest::getHeader;

    private final String UP_TRACE_KEY = DynamicConfigManger.getConfigValue("up.trace.key");

    @Override
    public void filter(SelfChain selfChain) throws ServletException, IOException {

        String expectCostTime = TraceExtraHelper.addExtra(GETTER, selfChain.request(), LinkKeyConst.TC_TT);


        CustomHttpServletRequestWrapper request = new CustomHttpServletRequestWrapper(selfChain.request());
        String upTraceHeader = request.getHeader(UP_TRACE_KEY);
        // 兼容上游已经存在的trace系统，例如虽然介入了LongHeng，但是在使用者之前，就已经有不一致的trace key 需要处理
        // 同理对于其他key的转换也可以参考改实现
        if (ObjectUtil.isEmpty(request.getHeader(LinkKeyConst.getTraceIdKey())) && ObjectUtil.isNotEmpty(upTraceHeader)) {
            log.debug("up trace key:{} convert start", UP_TRACE_KEY);
            request.putHeader(LinkKeyConst.getTraceIdKey(), upTraceHeader);
            request.removeHeader(UP_TRACE_KEY);
            try (TraceClose traceClose = traceHolder.newTraceScope(GETTER, request)) {
                selfChain.proceed();
            }
            return;
        }
        // 正常情况
        try (TraceClose traceClose = traceHolder.newTraceScope()) {
            // 接收上游链路耗时要求
//            String expectCostTime = GETTER.get(selfChain.request(), LinkKeyConst.TC_TT);
//            traceClose.getTrace().addExtra(LinkKeyConst.TC_TT, expectCostTime);
            if (log.isDebugEnabled()) {
                log.debug("url:{} your upstream expect you should under {} ms return",  selfChain.request().getRequestURI(), expectCostTime);
            }
            selfChain.proceed();
        }

    }

    // 让trace 越早出现越好
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }


    static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, List<String>> headers;

        public CustomHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            this.headers = initHeaders(request);
        }

        private static Map<String, List<String>> initHeaders(HttpServletRequest request) {
            Map<String, List<String>> headers = new LinkedHashMap<>(16);
            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                headers.put(name, Collections.list(request.getHeaders(name)));
            }
            return headers;
        }

        // Override header accessors to not expose forwarded headers

        @Override
        @Nullable
        public String getHeader(String name) {
            List<String> value = this.headers.get(name);
            return (CollectionUtils.isEmpty(value) ? null : value.get(0));
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> value = this.headers.get(name);
            return (Collections.enumeration(value != null ? value : Collections.emptySet()));
        }

        public void putHeader(String key, String value) {
            headers.put(key, Lists.newArrayList(value));
        }

        public void putIfAbsent(String key, String value) {
            headers.putIfAbsent(key, Lists.newArrayList(value));
        }

        public void removeHeader(String key) {
            headers.remove(key);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(this.headers.keySet());
        }

    }

}

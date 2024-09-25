package org.lkg.rpc.feign;

import com.google.common.collect.Lists;
import feign.Request;
import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lkg.constant.LinkKeyConst;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.metric.rpc.feign.SelfFeignInterceptor;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/24 9:33 PM
 */
@AllArgsConstructor
@Slf4j
public class FeignTraceInterceptor implements SelfFeignInterceptor {

    private final TraceHolder traceHolder;

    private final static FullLinkPropagation.Setter<Map<String,Collection<String>>> SETTER = (Map<String, Collection<String>> headers, String key, String value) -> {
        if (headers.containsKey(key)) {
            if (log.isDebugEnabled()) {
                log.debug("key:{} exist header", key);
            }
        }
        headers.putIfAbsent(key, Lists.newArrayList(value));
    };

    @Override
    public Response interceptor(FeignChain feignChain) throws IOException {
        Request request = feignChain.request();
        try (TraceClose traceClose = traceHolder.newTraceScope(SETTER, new HashMap<>(request.headers()))) {
            // expect downstream
            traceClose.getTrace().addExtra(LinkKeyConst.TC_TT, String.valueOf(feignChain.options().readTimeout()));
            return feignChain.process();
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}

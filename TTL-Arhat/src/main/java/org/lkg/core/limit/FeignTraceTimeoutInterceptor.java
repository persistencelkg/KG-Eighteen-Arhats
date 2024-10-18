package org.lkg.core.limit;

import feign.Request;
import io.micrometer.core.instrument.Tag;
import lombok.extern.java.Log;
import org.lkg.core.config.TraceLogEnum;
import org.lkg.metric.rpc.feign.FeignMetaDataContext;
import org.lkg.metric.rpc.feign.SelfFeignInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/17 5:02 PM
 */
public class FeignTraceTimeoutInterceptor implements SelfFeignInterceptor {
    private final Map<String, TraceTimeoutLimiter> URLTraceMap = new ConcurrentHashMap<>();

    @Override
    public feign.Response interceptor(FeignChain feignChain) throws IOException {
        Request request = feignChain.request();
        String url = request.url();
        FeignMetaDataContext.FeignMetaData feignMetaContext = FeignMetaDataContext.getFeignMetaContext(url);
        if (Objects.isNull(feignMetaContext)) {
            return feignChain.process();
        }
        String key = feignMetaContext.getServer() + url;
        Request.Options options = feignChain.options();
        String namespace = "third.success";
        int current = options.readTimeoutMillis();
        long newTimeout = URLTraceMap.computeIfAbsent(key, ref -> new TraceTimeoutLimiter(namespace, Tag.of("url", url))).tryCheckAndNextTimeout(current, TraceLogEnum.Feign);
        return feignChain.process(request, new Request.Options(options.connectTimeoutMillis(), TimeUnit.MILLISECONDS, newTimeout, TimeUnit.MILLISECONDS, options.isFollowRedirects()));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

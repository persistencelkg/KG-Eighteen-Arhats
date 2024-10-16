package org.lkg.log;

import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.lkg.metric.rpc.feign.SelfFeignInterceptor;
import org.lkg.simple.UrlUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/15 2:29 PM
 */
@Component
public class FeignLogInterceptor implements SelfFeignInterceptor {
    @Override
    public Response interceptor(FeignChain feignChain) throws IOException {
        Request request = feignChain.request();
        RequestTemplate requestTemplate = request.requestTemplate();
        RequestLogAspect.LogTarget logTarget = RequestLogAspect.getFeignLogTarget();
        HashMap<String, Object> map = new HashMap<>();
        if (Objects.nonNull(logTarget)) {
            logTarget.setIpHost("");
            logTarget.setUrl(UrlUtil.parseUri(request.url()));
            Map<String, Collection<String>> headers = requestTemplate.headers();
            for (String s : headers.keySet()) {
                map.put(s, headers.get(s));
            }
            logTarget.setHeader(map);
        }
        return feignChain.process();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }
}

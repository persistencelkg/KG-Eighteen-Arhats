package org.lkg.metric.rpc.feign;

import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.metric.rpc.http.MetricHttp;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import static org.lkg.exception.ExceptionSystemConst.TIMEOUT_MAYBE_ERR_CODE;
import static org.lkg.metric.rpc.RpcTagConstant.HTTP_NAME_SPACE;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 3:53 PM
 */
@Slf4j
public class MetricFeignRequestInterceptor implements SelfFeignInterceptor {


    @Override
    public Response interceptor(FeignChain feignChain) throws IOException {
        Request request = feignChain.request();
        String url = request.url();
        Response response = null;
        if (log.isDebugEnabled()) {
            log.debug("{} match {}", url, FeignMetaDataContext.getFeignMetaContext(url));
        }
        RequestTemplate requestTemplate = feignChain.request().requestTemplate();
        // 为请求携带日志
//        requestTemplate.header(requestTemplate.feignTarget().name(), "test-lkg");
        try {
            return response = feignChain.process();
        } finally {
            recordTime(url, response, feignChain.getStartTime());
        }
    }

    private void recordTime(String url, Response response, long start) {
        int code = Objects.nonNull(response) ? response.status() : TIMEOUT_MAYBE_ERR_CODE;
        MetricHttp.httpMetricRecord(code, url, start);
    }


    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }
}

package org.lkg.metric.rpc.http;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.exception.ExceptionSystemConst;
import org.lkg.simple.ObjectUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static org.lkg.metric.rpc.RpcTagConstant.HTTP_NAME_SPACE;
import static org.lkg.metric.rpc.RpcTagConstant.HTTP_URL;

/**
 * Description: 提供统一的http client request、Response拦截器
 * Author: 李开广
 * Date: 2024/9/5 11:16 AM
 */
public class MetricHttpProcessor implements HttpProcessor {

    private static MetricHttpProcessor processor;

    public static MetricHttpProcessor getInstance() {
        if (Objects.isNull(processor)) {
            processor = new MetricHttpProcessor();
        }
        return processor;
    }

    private MetricHttpProcessor() {
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        context.setAttribute(HTTP_NAME_SPACE, System.currentTimeMillis());
        context.setAttribute(HTTP_URL, getUrl(request));
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        Long attribute = (Long) context.getAttribute(HTTP_NAME_SPACE);
        String urlAttribute = ((String) context.getAttribute(HTTP_URL));
        if (ObjectUtil.isEmpty(urlAttribute)) {
            return;
        }
        Long start = Optional.ofNullable(attribute).orElse(System.currentTimeMillis());
        int code = Objects.nonNull(response) && response.getStatusLine().getStatusCode() < 400 ? response.getStatusLine().getStatusCode() : ExceptionSystemConst.TIMEOUT_MAYBE_ERR_CODE;
        httpMetricRecord(code, urlAttribute, start);
    }

    public static void httpMetricRecord(int code, String url, long start) {
        httpMetricRecord(code >= 200 && code < 400, code, url, start);
    }


    public static void httpMetricRecord(boolean suc, int code, String url, long start) {
        String namespace = HTTP_NAME_SPACE + (suc ? "success" : "fail");
        Timer.builder(namespace)
                .tags(Tags.concat(Tags.of("url", url), Tags.of("code", String.valueOf(code))))
                .register(LongHengMeterRegistry.getInstance())
                .record(Duration.ofMillis(System.currentTimeMillis() - start));
    }

    private String getUrl(HttpRequest request) {
        if (request instanceof HttpRequestWrapper) {
            return ((HttpRequestWrapper) request).getOriginal().getRequestLine().getUri();
        }
        return request.getRequestLine().getUri();
    }
}

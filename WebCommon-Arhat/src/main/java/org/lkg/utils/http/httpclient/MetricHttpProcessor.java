package org.lkg.utils.http.httpclient;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.protocol.*;
import org.apache.http.util.VersionInfo;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.exception.ExceptionSystemConst;
import org.lkg.metric.rpc.http.MetricHttp;
import org.lkg.request.InternalRequest;
import org.lkg.simple.ObjectUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.lkg.metric.rpc.RpcTagConstant.HTTP_NAME_SPACE;
import static org.lkg.metric.rpc.RpcTagConstant.HTTP_URL;

/**
 * Description: 提供统一的http client request、Response拦截器
 * Author: 李开广
 * Date: 2024/9/5 11:16 AM
 */
@Slf4j
public class MetricHttpProcessor implements HttpProcessor {

    private static MetricHttpProcessor processor;
    private static final FullLinkPropagation.Setter<HttpRequest> SETTER = ((carrier, key, value) -> {
        if (!carrier.containsHeader(key)) {
            carrier.setHeader(key, value);
        }
    });
    private static final String TRACE_HEADER = "trace.header";

    public synchronized static MetricHttpProcessor getInstance() {
        if (Objects.isNull(processor)) {
            processor = new MetricHttpProcessor();
        }
        return processor;
    }

    private final List<HttpRequestInterceptor> MIN_LIMIT_REQUEST = new ArrayList<>();

    private MetricHttpProcessor() {
        // 必需
        MIN_LIMIT_REQUEST.add(new RequestTargetHost());
        // 必需
        MIN_LIMIT_REQUEST.add(new RequestContent());
        MIN_LIMIT_REQUEST.add((request, context) -> {
            request.setHeader(HTTP.CONTENT_TYPE, InternalRequest.BodyEnum.RAW.getContentTypeValue());

        });

        // 非必须，调用三方建议一定加上，内网可省
        MIN_LIMIT_REQUEST.add(new RequestUserAgent(getUserAgent()));
    }

    private String getUserAgent() {
        String userAgentCopy = System.getProperty("http.agent");
        if (userAgentCopy == null) {
            userAgentCopy = VersionInfo.getUserAgent("Apache-HttpClient",
                    "org.apache.http.client", getClass());
        }
        return userAgentCopy;
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        // metric header
        context.setAttribute(HTTP_NAME_SPACE, System.currentTimeMillis());
        context.setAttribute(HTTP_URL, getUrl(request));

        // trace header
        TraceHolder instance = TraceHolder.getInstance();

        if (Objects.nonNull(instance)) {
            TraceClose traceClose = instance.newTraceScope(SETTER, request);
            log.info("my trace:{}", traceClose.getTrace().getTraceId());
            context.setAttribute(TRACE_HEADER, traceClose);
        }
        // Necessary header
        for (HttpRequestInterceptor httpRequestInterceptor : MIN_LIMIT_REQUEST) {
            httpRequestInterceptor.process(request, context);
        }
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (Objects.nonNull(context.getAttribute(TRACE_HEADER))) {
            TraceClose traceClose = (TraceClose) context.getAttribute(TRACE_HEADER);
            traceClose.close();
        }

        Long attribute = (Long) context.getAttribute(HTTP_NAME_SPACE);
        String urlAttribute = ((String) context.getAttribute(HTTP_URL));
        if (ObjectUtil.isEmpty(urlAttribute)) {
            return;
        }
        Long start = Optional.ofNullable(attribute).orElse(System.currentTimeMillis());
        int code = Objects.nonNull(response) && response.getStatusLine().getStatusCode() < 400 ? response.getStatusLine().getStatusCode() : ExceptionSystemConst.TIMEOUT_MAYBE_ERR_CODE;
        MetricHttp.httpMetricRecord(code, urlAttribute, start);
    }

    private String getUrl(HttpRequest request) {
        if (request instanceof HttpRequestWrapper) {
            // 记录真实的url全路径 https://xxx/a/b/c
            // return ((HttpRequestWrapper) request).getOriginal().getRequestLine().getUri();
            return request.getRequestLine().getUri();
        }
        return request.getRequestLine().getUri();
    }
}

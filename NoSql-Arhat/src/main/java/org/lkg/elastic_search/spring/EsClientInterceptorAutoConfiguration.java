package org.lkg.elastic_search.spring;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.lkg.core.config.EnableLongHengMetric;
import org.lkg.core.config.OnTraceTimeoutEnable;
import org.lkg.core.config.TraceLogEnum;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.limit.TraceTimeoutLimiter;
import org.lkg.exception.ExceptionSystemConst;
import org.lkg.retry.RetryInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/21 5:02 PM
 */
@Configuration
public class EsClientInterceptorAutoConfiguration {


    @Configuration
    @OnTraceTimeoutEnable
    static class TraceTimeoutAutoConfiguration {

        @Bean
        public RetryInterceptor esBulkRetryInterceptor() {
            return () -> {
                TraceTimeoutLimiter.getAndCheck(TraceLogEnum.ElasticSearch);
            };
        }
    }


    @Configuration
    @EnableLongHengMetric
    static class MetricAutoConfiguration {

        private static final String esNamespace = "es.req.";


        @AllArgsConstructor
        private static class InjectTraceTimeoutRequestInterceptor implements HttpRequestInterceptor {

            @Autowired
            private RetryInterceptor retryInterceptor;

            @Override
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                if (Objects.nonNull(retryInterceptor)) {
                    retryInterceptor.preHand();
                }
                context.setAttribute(esNamespace, System.currentTimeMillis());
            }
        }

        @Bean
        public HttpRequestInterceptor esAysncHttpRequestInterceptor(ObjectProvider<RetryInterceptor> esBulkRetryInterceptor) {
            return new InjectTraceTimeoutRequestInterceptor(esBulkRetryInterceptor.getIfAvailable());
        }

        @Bean
        public HttpResponseInterceptor esAsyncHttpResponseInterceptor() {
            return (response, context) -> {
                Long start = (Long) context.getAttribute(esNamespace);
                int code = Objects.nonNull(response.getStatusLine()) ? response.getStatusLine().getStatusCode() : ExceptionSystemConst.TIMEOUT_MAYBE_ERR_CODE;
                String namespace = TraceLogEnum.ElasticSearch.getNameSpace(code >= 200 && code < 400);
                Timer.builder(namespace)
                        .tags(Tags.of("code", String.valueOf(code)))
                        .register(LongHengMeterRegistry.getInstance())
                        .record(Duration.ofMillis(System.currentTimeMillis() - start));
            };
        }


    }
}

package org.lkg.metric.api;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.simple.ObjectUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/5 7:23 PM
 */
public class MetricFilter implements CommonFilter {
    @Override
    public void filter(SelfChain selfChain) {
        long start = System.currentTimeMillis();
        boolean suc = true;
        HttpServletRequest request = selfChain.request();
        System.out.println("参数" + request.getParameterMap());
        try {
            selfChain.proceed();
        } catch (ServletException | IOException e) {
            suc = false;
            throw new RuntimeException(e);
        } finally {
            Object uri = selfChain.request().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            if (ObjectUtil.isNotEmpty(uri)) {
                String namespace = "api." + (suc ? "success" : "fail");
                Timer.builder(namespace)
                        .tags(Tags.of("url", uri.toString()).and("code", String.valueOf(selfChain.response().getStatus())))
                        .register(LongHengMeterRegistry.getInstance())
                        .record(Duration.ofMillis(System.currentTimeMillis() - start));
            }

        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}

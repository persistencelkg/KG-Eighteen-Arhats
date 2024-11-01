package org.lkg.metric.rpc.feign;

import feign.Request;
import feign.Response;
import org.lkg.simple.matcher.AntPathMatcher;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 2:22 PM
 */
public interface SelfFeignInterceptor extends Ordered {

    Response interceptor(FeignChain feignChain) throws IOException;

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>(new AntPathMatcher().extractUriTemplateVariables("http://aaa.w.com/test-2", "http://aaa.w.com/test-2/atm-coupon?a=2&b=3"));
        System.out.println(params);
    }

    interface FeignChain {

        Response process() throws IOException;

        Response process(Request request) throws IOException;

        Response process(Request request, Request.Options options) throws IOException;

        long getStartTime();

        Request request();

        Request.Options options();
    }
}

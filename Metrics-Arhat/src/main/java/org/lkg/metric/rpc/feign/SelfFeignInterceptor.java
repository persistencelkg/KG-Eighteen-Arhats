package org.lkg.metric.rpc.feign;

import feign.Request;
import feign.Response;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.time.Duration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 2:22 PM
 */
public interface SelfFeignInterceptor extends Ordered {

    Response interceptor(FeignChain feignChain) throws IOException;

    interface FeignChain {

        Response process() throws IOException;

        Response process(Request request) throws IOException;

        Response process(Request request, Request.Options options) throws IOException;

        long getStartTime();

        Request request();

        Request.Options options();
    }
}

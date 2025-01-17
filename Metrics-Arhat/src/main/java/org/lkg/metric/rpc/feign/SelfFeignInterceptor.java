package org.lkg.metric.rpc.feign;

import feign.Request;
import feign.Response;
import org.springframework.core.Ordered;

import java.io.IOException;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 2:22 PM
 */
public interface SelfFeignInterceptor extends Ordered {

    Response interceptor(FeignChain feignChain) throws IOException;

    /**
     * 是否拦截返回结果，默认false 如果是就会先将 interceptResult = false 对应的interceptor实现
     * 最后在单独处理，并拦截结果
     */
    default boolean interceptResult() {
        return false;
    }

    /**
     * 是否拦截多次结果
     * @return 默认只拦截1次
     */
   default boolean isInterceptResultContinue() {
        return false;
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

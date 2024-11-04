package org.lkg.metric.rpc.feign;

import feign.Request;
import feign.Response;
import org.lkg.simple.ObjectUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 2:28 PM
 */
public abstract class AbstractFeignChain implements SelfFeignInterceptor.FeignChain {

    private final Request request;

    private final Request.Options options;

    private long start;

    private final Iterator<SelfFeignInterceptor> iterator;

    public AbstractFeignChain(Request request, Request.Options options, Iterator<SelfFeignInterceptor> iterator) {
        this.request = request;
        this.options = options;
        this.start = System.currentTimeMillis();
        this.iterator = iterator;
    }

    @Override
    public Response process() throws IOException {
        return process(request, options);
    }

    @Override
    public Response process(Request request, Request.Options options) throws IOException {
        List<SelfFeignInterceptor> handleResultInteceptorList = new ArrayList<>();
        this.start = System.currentTimeMillis();
        // only enhance
        iterator.forEachRemaining((val) -> {
            if (val.interceptResult()) {
                handleResultInteceptorList.add(val);
                return;
            }
            try {
                iterator.next().interceptor(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // change result
        Response interceptor = null;
        if (ObjectUtil.isNotEmpty(handleResultInteceptorList)) {
            for (SelfFeignInterceptor selfFeignInterceptor : handleResultInteceptorList) {
                interceptor = selfFeignInterceptor.interceptor(this);
                if (!selfFeignInterceptor.isInterceptResultContinue()) {
                    return interceptor;
                }
            }
            return interceptor;
        }
        // the bottom line result
        return doProcess(request, options);

    }

    protected abstract Response doProcess(Request request, Request.Options options) throws IOException;


    @Override
    public Response process(Request request) throws IOException {
        return process(request, options);
    }

    @Override
    public long getStartTime() {
        return this.start;
    }

    @Override
    public Request.Options options() {
        return this.options;
    }

    @Override
    public Request request() {
        return this.request;
    }
}

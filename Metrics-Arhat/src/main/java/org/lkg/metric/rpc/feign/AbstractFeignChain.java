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
        this.start = System.currentTimeMillis();
        return iterator.hasNext() ? iterator.next().interceptor(this) : doProcess(request, options);

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

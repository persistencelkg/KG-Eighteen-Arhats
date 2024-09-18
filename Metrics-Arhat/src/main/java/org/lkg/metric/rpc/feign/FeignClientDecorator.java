package org.lkg.metric.rpc.feign;

import feign.Client;
import feign.Request;
import feign.Response;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 2:22 PM
 */
public class FeignClientDecorator implements Client {

    private final Client client;

    private final LinkedList<SelfFeignInterceptor> interceptorLinkedHashSet;

    public FeignClientDecorator(Collection<? extends SelfFeignInterceptor> selfFeignInterceptors, Client client) {
        Objects.requireNonNull(selfFeignInterceptors, "selfFeignInterceptors not empty");
        this.interceptorLinkedHashSet = new LinkedList<>(selfFeignInterceptors);
        this.interceptorLinkedHashSet.sort(Comparator.comparing(SelfFeignInterceptor::getOrder));
        this.client = client;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return new Chain(request, options).process();
    }


    class Chain extends AbstractFeignChain {

        public Chain(Request request, Request.Options options) {
            super(request, options, interceptorLinkedHashSet.iterator());
        }

        @Override
        protected Response doProcess(Request request, Request.Options options) throws IOException {
            return client.execute(request, options);
        }
    }
}

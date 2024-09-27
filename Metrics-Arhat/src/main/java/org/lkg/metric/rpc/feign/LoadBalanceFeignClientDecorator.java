package org.lkg.metric.rpc.feign;

import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 2:22 PM
 */
public class LoadBalanceFeignClientDecorator extends LoadBalancerFeignClient {


    private final LinkedList<SelfFeignInterceptor> interceptorLinkedHashSet;

    public LoadBalanceFeignClientDecorator(Collection<? extends SelfFeignInterceptor> selfFeignInterceptors,
                                           Client client,
                                           SpringClientFactory springClientFactory,
                                           CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory) {
        super(client, cachingSpringLoadBalancerFactory, springClientFactory);
        Objects.requireNonNull(selfFeignInterceptors, "selfFeignInterceptors not empty");
        this.interceptorLinkedHashSet = new LinkedList<>(selfFeignInterceptors);
        this.interceptorLinkedHashSet.sort(Comparator.comparing(SelfFeignInterceptor::getOrder));
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
            return LoadBalanceFeignClientDecorator.super.execute(request, options);
        }
    }
}

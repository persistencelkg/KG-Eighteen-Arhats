package org.lkg.metric.rpc.feign;

import feign.Client;
import feign.Contract;
import feign.Feign;
import org.lkg.utils.ReflectUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.core.env.Environment;

import java.util.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 4:18 PM
 */
public class FeignContextDecorator extends FeignContext {

    private final List<SelfFeignInterceptor> selfFeignInterceptors;
    private final Environment environment;
    private final FeignContext feignContext;
    private final BeanFactory beanFactory;
    private CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory;
    private SpringClientFactory springClientFactory;

    public FeignContextDecorator(List<SelfFeignInterceptor> list, Environment environment, BeanFactory beanFactory, FeignContext context) {
        this.selfFeignInterceptors = new LinkedList<>(list);
        this.environment = environment;
        this.feignContext = context;
        this.beanFactory = beanFactory;
    }

    @Override
    public <T> T getInstance(String name, Class<T> type) {
        T instance = this.feignContext.getInstance(name, type);
        // client执行器的拦截
        // 结果可能为空, 空值会在判断时被默认为false，尽管是对应类型的空值，也会遗漏校验，这种时候采用静态类型诊断
        if (Client.class.isAssignableFrom(type)) {
            Client client = Objects.isNull(instance) ? new Client.Default(null, null) : ((Client) instance);
            return (T) new FeignClientDecorator(selfFeignInterceptors, client);
        }

        // 元数据的拦截
        if (Contract.class.isAssignableFrom(type)) {
            Contract contract = Objects.isNull(instance) ? new Contract.Default() : ((Contract) instance);
            return (T) FeignMetaDataMethodInterceptor.getProxy(environment, contract);
        }
        // 通过feign builder 创建client拦截
        if (Feign.Builder.class.isAssignableFrom(type)) {
            Feign.Builder feignBuilder = Objects.isNull(instance) ? Feign.builder() : ((Feign.Builder) instance);
            Client client = ReflectUtil.findField(feignBuilder, Client.class, "client");
            if (Objects.nonNull(client)) {
                if (client instanceof LoadBalancerFeignClient) {
                    feignBuilder.client(new LoadBalanceFeignClientDecorator(selfFeignInterceptors, ((LoadBalancerFeignClient) client).getDelegate(), springClientFactory(), cachingSpringLoadBalancerFactory()));
                } else {
                    feignBuilder.client(new FeignClientDecorator(selfFeignInterceptors, client));
                }
            }
        }
        return instance;
    }

    private CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory() {
        if (Objects.isNull(cachingSpringLoadBalancerFactory)) {
            cachingSpringLoadBalancerFactory = beanFactory.getBean(CachingSpringLoadBalancerFactory.class);
        }
        return cachingSpringLoadBalancerFactory;
    }
    private SpringClientFactory springClientFactory() {
        if (Objects.isNull(springClientFactory)) {
            springClientFactory = beanFactory.getBean(SpringClientFactory.class);
        }
        return springClientFactory;
    }


    @Override
    public <T> Map<String, T> getInstances(String name, Class<T> type) {
        Map<String, T> instances = this.feignContext.getInstances(name, type);
        Map<String, T> map = new HashMap<>();
        instances.forEach((k, v) -> {
            T obj = v;
            if (Client.class.isAssignableFrom(type)) {
                Client client = Objects.isNull(v) ? new Client.Default(null, null) : ((Client) v);
                obj = (T) new FeignClientDecorator(selfFeignInterceptors, client);
            } else if (Contract.class.isAssignableFrom(type)) {
                Contract contract = Objects.isNull(v) ? new Contract.Default() : ((Contract) v);
                obj = (T) FeignMetaDataMethodInterceptor.getProxy(environment, contract);
            } else if (Feign.Builder.class.isAssignableFrom(type)) {
                Feign.Builder feignBuilder = Objects.isNull(v) ? Feign.builder() : ((Feign.Builder) v);
                Client client = ReflectUtil.findField(feignBuilder, Client.class, "client");
                if (client != null) {
                    feignBuilder.client(new FeignClientDecorator(selfFeignInterceptors, client));
                }
                obj = (T) feignBuilder;
            }
            map.put(k, obj);
        });
        return map;
    }

    public static void main(String[] args) {
        Object a = null;
        System.out.println((a instanceof String));
    }
}

package org.lkg.metric.rpc.feign;

import feign.Contract;
import feign.MethodMetadata;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.lkg.enums.StringEnum;
import org.lkg.simple.ObjectUtil;
import org.lkg.simple.UrlUtil;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.env.Environment;

import java.io.File;
import java.net.URI;
import java.util.Collection;

/**
 * Description: 为了拦截feign的元数据，存储一些必要的上下文信息 便于功能的扩展 而做的AOP拦截器
 * Author: 李开广
 * Date: 2024/9/9 2:41 PM
 */
public class FeignMetaDataMethodInterceptor implements MethodInterceptor {


    private final Environment environment;

    public FeignMetaDataMethodInterceptor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object proceed = invocation.proceed();
        Object[] arguments = invocation.getArguments();
        if (!ObjectUtil.isEmpty(arguments) && arguments[0] instanceof Class
                && !ObjectUtil.isEmpty(proceed) && proceed instanceof Collection) {
            parseAndBuildFeignMetaData(((Class<?>) arguments[0]), (Collection<?>) proceed);
        }
        return proceed;
    }

    private void parseAndBuildFeignMetaData(Class<?> argument, Collection<?> proceed) {
        FeignClient annotation = argument.getAnnotation(FeignClient.class);
        if (ObjectUtil.isEmpty(argument)) {
            return;
        }
        // 先取绝对hostname or domain
        String url = annotation.url();
        if (ObjectUtil.isEmpty(url)) {
            url = environment.resolvePlaceholders(annotation.name());
        } else {
            url = environment.resolvePlaceholders(url);
        }
        if (ObjectUtil.isEmpty(url)) {
            url = environment.resolvePlaceholders(annotation.value());
        }
        // 拼接前缀
        String path = annotation.path();
        if (!ObjectUtil.isEmpty(path) && !path.startsWith(File.separator)) {
            path = File.separator + environment.resolvePlaceholders(path);
        }
        if (!url.startsWith(StringEnum.HTTP_PREFIX) || !url.startsWith(StringEnum.HTTPS_PREFIX)) {
            url = StringEnum.HTTP_PREFIX + url;
        }
        url = url + path;
        final String finalUrl = url;
        // 拼接相对路径
        proceed.stream().map(o -> ((MethodMetadata) o)).forEach(val -> {
            FeignMetaDataContext.addFeignUrl(finalUrl + val.template().url(), val);
        });

    }


    public static Object getProxy(Environment environment, Contract contract) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(contract);
        proxyFactoryBean.setProxyTargetClass(true);
        proxyFactoryBean.addAdvice(new FeignMetaDataMethodInterceptor(environment));
        return proxyFactoryBean.getObject();
    }

}

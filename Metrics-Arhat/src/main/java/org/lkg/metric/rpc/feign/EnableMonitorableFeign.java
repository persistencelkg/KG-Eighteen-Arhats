package org.lkg.metric.rpc.feign;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Description: 实现feign的监控 & 可扩展的拦截器
 * Author: 李开广
 * Date: 2025/1/3 3:40 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FeignMetricAutoConfiguration.class)
public @interface EnableMonitorableFeign {
}

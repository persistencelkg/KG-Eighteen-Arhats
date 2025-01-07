package org.lkg.core.config;

import org.lkg.spring.TraceTimeoutAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/17 5:08 PM
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnClass(FeignClient.class)
@ConditionalOnProperty(name = EnableTraceTimeOut.key, havingValue = "1")
@Import(TraceTimeoutAutoConfiguration.class)
public @interface EnableTraceTimeOut {
    String key ="ttl.check.enable";
}

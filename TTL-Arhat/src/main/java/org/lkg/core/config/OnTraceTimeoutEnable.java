package org.lkg.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;

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
@ConditionalOnProperty(name = OnTraceTimeoutEnable.key, havingValue = "1")
public @interface OnTraceTimeoutEnable {
    String key ="ttl.check.enable";
}

package org.lkg.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/15 11:20 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ConditionalOnProperty(value = LongHongConst.ENABLE_KEY, havingValue = "1", matchIfMissing = true)
public @interface EnableLongHengMetric {
}

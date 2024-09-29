package org.lkg.kafka.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/29 2:45 PM
 */
@Target(value = ElementType.TYPE)
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@ConditionalOnProperty(value = "more.kafka.enable", havingValue = "1") // 必须指定
public @interface OnEnableMoreKafka {
}

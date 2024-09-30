package org.lkg.rocketmq.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/30 5:08 PM
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnProperty(prefix = "more", value = "rocketmq")
public @interface OnEnableMoreRocket {
}

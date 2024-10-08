package org.lkg.rocketmq.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@ConditionalOnClass(MoreRocketMqConfig.class)
@ConditionalOnProperty(name = "more.rocketmq.enable", havingValue = "1", matchIfMissing = true)
public @interface OnEnableMoreRocket {
}

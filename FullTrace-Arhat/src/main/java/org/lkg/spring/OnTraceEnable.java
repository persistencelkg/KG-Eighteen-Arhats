package org.lkg.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

import static org.lkg.constant.LinkKeyConst.TRACE_BEAN_ENABLE_KEY;

/**
 * Description: 控制是否引入全链路 trace 能力
 * Author: 李开广
 * Date: 2024/9/24 8:47 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@ConditionalOnProperty(name = TRACE_BEAN_ENABLE_KEY, havingValue = "1", matchIfMissing = true)
public @interface OnTraceEnable {

}

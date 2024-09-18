package org.lkg.core;

import java.lang.annotation.*;

/**
 * Description: 动态key注解
 * Author: 李开广
 * Date: 2024/8/13 11:28 AM
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicKeyConfig {

    String key() default "";

    String def() default "";
}

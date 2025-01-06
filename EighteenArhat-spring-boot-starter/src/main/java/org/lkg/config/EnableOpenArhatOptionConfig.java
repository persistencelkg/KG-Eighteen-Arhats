package org.lkg.config;

import org.lkg.core.DynamicConfigImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2025/1/3 4:13 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DynamicConfigImportSelector.class)
public @interface EnableOpenArhatOptionConfig {

    DynamicConfigOption[] type() default {DynamicConfigOption.METRIC_CONFIG};
}

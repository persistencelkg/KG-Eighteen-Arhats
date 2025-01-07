package org.lkg.algorithm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2025/1/3 7:48 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(prefix = "spring.datasource.dynamic", value = "datasource")
public @interface EnableMoreDynamicDatasource {
}

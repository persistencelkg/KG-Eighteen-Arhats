package org.lkg.elastic_search.enums;

import java.lang.annotation.*;

/**
 * Description: 针对7.0以下自定义type，因为和pojo息息相关，所以不建议将type 放入到常量类去处理，这样也体现面向对象封装性
 * Author: 李开广
 * Date: 2024/10/10 11:08 AM
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsDoc {

    String type() default "_doc";

    String uniqueKey();
}

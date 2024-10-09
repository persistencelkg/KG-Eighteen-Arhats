package org.lkg.elastic_search.enums;

import java.lang.annotation.*;

/**
 * 指定目标字段是否需要建立全文索引
 * Description:
 * Author: 李开广
 * Date: 2024/10/9 1:41 PM
 */

@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TextIndex {

    EsFieldType value() default EsFieldType.TEXT;
}

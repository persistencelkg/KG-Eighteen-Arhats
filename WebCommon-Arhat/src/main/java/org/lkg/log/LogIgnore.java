package org.lkg.log;

import java.lang.annotation.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/15 11:40 AM
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogIgnore {
}

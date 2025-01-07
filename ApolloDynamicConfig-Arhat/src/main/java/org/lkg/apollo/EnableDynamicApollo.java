package org.lkg.apollo;

import org.lkg.config.ApolloConfigBeanFactoryPostProcessorInitializer;
import org.lkg.config.DynamicConfigAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2025/1/3 1:50 PM
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ApolloConfigBeanFactoryPostProcessorInitializer.class, DynamicConfigAutoConfiguration.class})
public @interface EnableDynamicApollo {
}

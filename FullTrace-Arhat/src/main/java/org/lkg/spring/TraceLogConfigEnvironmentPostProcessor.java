package org.lkg.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StopWatch;

import java.util.LinkedHashMap;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/25 5:10 PM
 */
public class TraceLogConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("logging.pattern.level", "%5p [%X{traceId:-}]");
        MapPropertySource propertySource = new MapPropertySource(
                "selfTraceLogConfig", map);
        environment.getPropertySources().addLast(propertySource);
    }
}

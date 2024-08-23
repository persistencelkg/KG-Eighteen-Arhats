package org.lkg.core.config;

import io.micrometer.core.instrument.step.StepRegistryConfig;
import org.lkg.core.DynamicConfigManger;

import java.time.Duration;

/**
 * Description: 按规范定义配置
 * Author: 李开广
 * Date: 2024/8/8 3:08 PM
 */
public class LongHengStepRegistryConfig implements StepRegistryConfig {
    @Override
    public String prefix() {
        return LongHongConst.KEY_PREFIX;
    }

    @Override
    public String get(String key) {
        return DynamicConfigManger.getConfigValue(key);
    }
}

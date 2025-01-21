package org.lkg.config;

import lombok.Data;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.DynamicKeyConfig;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/13 11:27 AM
 */
@DynamicKeyConfig(key = "metric-config")
@Data
public class MetricCollectorConfig {


    private List<String> deptBlackList;

    private List<String> serverNameBlackList;

    private Integer batchSize;

    private Integer enable = 1;

    public static MetricCollectorConfig getInstance() {
        return DynamicConfigManger.getAnnotationConfig(MetricCollectorConfig.class);
    }
}

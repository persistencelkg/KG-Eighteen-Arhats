package org.lkg.config;

import lombok.Data;
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

}

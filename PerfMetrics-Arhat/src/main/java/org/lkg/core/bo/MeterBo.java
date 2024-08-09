package org.lkg.core.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lkg.core.config.LongHongMonitorConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 2:03 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterBo {

    private String namespace;

    private long count;

    private double val;

    private double max;

    private double min;

    private double mean;

    @JsonIgnore
    private TimeUnit timeUnit;

    private Map<String, String> tag;

    public void init() {
        tag = new HashMap<>();
        LongHongMonitorConfig instance = LongHongMonitorConfig.getInstance();
    }
}

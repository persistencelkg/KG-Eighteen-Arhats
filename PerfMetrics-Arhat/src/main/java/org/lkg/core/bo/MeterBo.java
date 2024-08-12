package org.lkg.core.bo;

import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.config.LongHongConst;
import org.lkg.core.config.LongHongMonitorConfig;
import org.lkg.simple.NetUtils;
import org.lkg.simple.ObjectUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    // 监控周期内总数
    private double total;

    // 全局统计
    private double count;

    // 监控周期内总数
    private double max;

    private double min;

    private double mean;

    private double p95;
    private double p99;
    private double p995;
    private double p999;

    // base unit for gauge store：bytes、rows、threads
    private String baseUnit;

    private Map<String, String> tagMap;

    public void init() {
        tagMap = new HashMap<>();

        LongHongMonitorConfig instance = LongHongMonitorConfig.getInstance();
        if (!ObjectUtil.isEmpty(instance)) {
            // 默认tag
            tagMap.put(LongHongConst.TagConst.IP, NetUtils.getLocalAddress());
            tagMap.put(LongHongConst.TagConst.SERVER_NAME, Optional.ofNullable(DynamicConfigManger.getServerName()).orElseGet(instance::getServerName));
            tagMap.put(LongHongConst.TagConst.ENV, Optional.ofNullable(DynamicConfigManger.getEnv()).orElseGet(instance::getEnv));
            tagMap.put(LongHongConst.TagConst.DEPT, instance.getDept());
        }
    }

    public void addTag(Tag tag) {
        if (ObjectUtil.isEmpty(tag)) {
            return;
        }
        tagMap.put(tag.getKey(), tag.getValue());
    }
}

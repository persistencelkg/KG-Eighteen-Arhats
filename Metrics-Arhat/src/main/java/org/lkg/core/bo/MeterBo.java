package org.lkg.core.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.config.LongHongConst;
import org.lkg.core.config.LongHongAlarmConfig;
import org.lkg.simple.NetUtils;
import org.lkg.simple.ObjectUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.lkg.core.config.LongHongConst.TagConst.INTERNAL_TAG;

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

    private String flag = "longheng";

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

        LongHongAlarmConfig instance = LongHongAlarmConfig.getInstance();
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


    @JsonIgnore
    public Map<String, String> getNotInternalTag() {
        HashMap<String, String> map = new HashMap<>(tagMap);
        INTERNAL_TAG.forEach(map::remove);
        return map;
    }
}

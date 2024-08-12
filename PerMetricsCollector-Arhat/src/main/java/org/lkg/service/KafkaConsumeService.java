package org.lkg.service;

import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.config.LongHongConst;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.simple.ObjectUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/12 9:09 PM
 */
@Service
public class KafkaConsumeService {

    @Resource
    private InfluxDbService influxDbService;

    private final LongHengMeterRegistry longHengMeterRegistry = LongHengMeterRegistry.getInstance();

    public void write(List<MeterBo> list) {
        if (ObjectUtil.isEmpty(list)) {
            return;
        }
        List<Point> points = list.stream().map(this::convertToPoint).collect(Collectors.toList());
        BatchPoints batchPoints = BatchPoints.builder().points(points).build();
        influxDbService.batchSavePoint(batchPoints);
        // TODO 监控自身组件
    }

    private Point convertToPoint(MeterBo dto) {
        String department = dto.getTagMap().get(LongHongConst.TagConst.DEPT);
        // 表
        String measurement = "longheng_for_" + department;
        Point.Builder builder = Point.measurement(measurement);
        // 字段
        builder.addField("count", dto.getCount());
        builder.addField("total", dto.getTotal());
        builder.addField("avg", dto.getMean());
        builder.addField("max", dto.getMax());
        builder.addField("min", dto.getMin());
        builder.addField("p995", dto.getP995());
        builder.addField("p95", dto.getP95());
        builder.addField("p99", dto.getP99());
        builder.addField("p999", dto.getP999());
        Map<String, String> tags = new HashMap<>(8);
        dto.getTagMap().forEach((k, v) -> {
            tags.put(k, v.replace("\n", " "));
        });
        // 索引
        builder.tag(tags).tag("longheng.ns", dto.getNamespace());
        builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        return builder.build();
    }

}

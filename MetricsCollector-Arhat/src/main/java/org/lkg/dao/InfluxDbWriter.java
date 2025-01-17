package org.lkg.dao;

import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.config.LongHongConst;
import org.lkg.service.InfluxDbService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/13 2:50 PM
 */
//@Service
public class InfluxDbWriter extends AbstractDataWriter<Point>{

    @Resource private InfluxDbService influxDbService;


    @Override
    protected void customBatchWrite(String dept, List<Point> list) {
        BatchPoints batchPoints = BatchPoints.builder().points(list).build();
        influxDbService.batchSavePoint(batchPoints);
        // TODO 业务扩大后， 可根据部门区分数据源做隔离
    }

    @Override
    public Point convert(MeterBo dto) {

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


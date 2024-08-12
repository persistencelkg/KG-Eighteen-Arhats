package org.lkg.service;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.lkg.config.InfluxDbConfig;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/12 8:57 PM
 */
@Service
@Slf4j
public class InfluxDbService {

    @Resource private InfluxDbConfig influxDbConfig;

    private static InfluxDB influxDB;

    @PostConstruct
    public void init() {
        influxDB = initInfluxDb();
        log.info(">> init influx db config:{}", influxDbConfig);
    }

    private InfluxDB initInfluxDb() {
        influxDB = InfluxDBFactory.connect(influxDbConfig.getUrl(), influxDbConfig.getUserName(), influxDbConfig.getPassword());
        influxDB.setDatabase(influxDbConfig.getDatabase());
        return influxDB;
    }


    public void batchSavePoint(BatchPoints batchPoints) {
        influxDB.write(batchPoints);
    }


}

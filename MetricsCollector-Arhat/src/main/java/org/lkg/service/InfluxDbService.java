package org.lkg.service;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.lkg.config.InfluxDbConfig;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/12 8:57 PM
 */
//@Service
@Slf4j
public class InfluxDbService {

    @Resource private InfluxDbConfig influxDbConfig;

    private static InfluxDB influxDB;

    @PostConstruct
    public void init() {
        influxDB = initInfluxDb("default");
        log.info(">> init influx db config:{}", influxDbConfig);
    }

    public InfluxDB initInfluxDb(String dept) {
        Map<String, InfluxDbConfig.DbConfig> config = influxDbConfig.getConfig();
        InfluxDbConfig.DbConfig dbConfig = config.get(dept);
        influxDB = InfluxDBFactory.connect(dbConfig.getUrl(), dbConfig.getUserName(), dbConfig.getPassword());
        influxDB.setDatabase(dbConfig.getDatabase());
        return influxDB;
    }


    public void batchSavePoint(BatchPoints batchPoints) {
        influxDB.write(batchPoints);
    }


}

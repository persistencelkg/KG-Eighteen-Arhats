package org.lkg.metric.sql;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricCoreExecutor;

import java.time.Duration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/14 3:02 PM
 */
@Slf4j
public class SqlEventTracker {

    private static final String SUC = "sql.suc";

    private static final String FAIL = "sql.fail";

    public static void monitorSql(String sql, boolean suc, long startTime) {
        MetricCoreExecutor.execute(() -> {
                    LongHengMeterRegistry instance = LongHengMeterRegistry.getInstance();
                    String namespace = suc ? SUC : FAIL;
                    try {
                        Tags tag = Tags.of("sql", FuzzySqlUtil.cleanStatement(sql));
                        Timer.builder(namespace)
                                .tags(tag)
                                .register(instance)
                                .record((System.nanoTime() - startTime) / 1000, instance.getBaseTimeUnit());
                    } catch (JSQLParserException e) {
                        log.error("sql:{} parse error", sql);
                    }
                }
        );
    }
}

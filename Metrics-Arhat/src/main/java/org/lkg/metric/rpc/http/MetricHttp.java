package org.lkg.metric.rpc.http;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.lkg.core.init.LongHengMeterRegistry;

import java.time.Duration;

import static org.lkg.metric.rpc.RpcTagConstant.HTTP_NAME_SPACE;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/28 8:15 PM
 */
public class MetricHttp {


    public static void httpMetricRecord(int code, String url, long start) {
        httpMetricRecord(code >= 200 && code < 400, code, url, start);
    }


    public static void httpMetricRecord(boolean suc, int code, String url, long start) {
        String namespace = HTTP_NAME_SPACE + (suc ? "success" : "fail");
        Timer.builder(namespace)
                .tags(Tags.concat(Tags.of("url", url), Tags.of("code", String.valueOf(code))))
                .register(LongHengMeterRegistry.getInstance())
                .record(Duration.ofMillis(System.currentTimeMillis() - start));
    }
}

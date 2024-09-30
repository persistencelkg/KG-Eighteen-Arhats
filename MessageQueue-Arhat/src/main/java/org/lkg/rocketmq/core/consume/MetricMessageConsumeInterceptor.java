package org.lkg.rocketmq.core.consume;

import io.micrometer.core.instrument.Timer;
import org.apache.rocketmq.common.message.MessageExt;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.MetricCoreExecutor;

import java.time.Duration;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/30 3:43 PM
 */
public class MetricMessageConsumeInterceptor implements ConsumeMessageProcessJoinPointInterceptor {

    @Override
    public Object intercept(SelfChain selfChain) {
        long start = System.nanoTime();
        MessageExt arg = (MessageExt) selfChain.args()[0];
        boolean suc = true;
        try {
            return selfChain.process();
        } catch (Throwable e) {
            suc = false;
            throw new RuntimeException(e);
        } finally {
            String res = suc ? "suc" : "fail";
            MetricCoreExecutor.execute(() -> {
                Timer.builder("rocketmq.consume")
                        .tags("topic", arg.getTopic(), "res", res)
                        .register(LongHengMeterRegistry.getInstance())
                        .record(Duration.ofNanos(System.nanoTime() - start));
            });
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }
}

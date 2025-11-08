package org.lkg.creatable_pattern.factory.processors;

import lombok.extern.slf4j.Slf4j;
import org.lkg.creatable_pattern.factory.StageContext;
import org.lkg.creatable_pattern.factory.StageStepProcessor;
import org.lkg.creatable_pattern.factory.proxy.OrderProxy;
import org.lkg.creatable_pattern.factory.proxy.PayProxy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/23 7:24 PM
 */
@Slf4j
public class OrderInitialProcessor extends StageStepProcessor {

    // 代理请求

    private OrderProxy orderProxy;
    private PayProxy payProxy;

    @Override
    protected void doProcess(StageContext stageContext) {
        // 编排
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(

                CompletableFuture.supplyAsync(() -> 0),

                CompletableFuture.supplyAsync(() -> 1)

        );

        try {
            // 获取期望的tc - tt
            voidCompletableFuture.get(1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void fallBackProcess(StageContext stageContext) {
        // 降级措施
    }
}

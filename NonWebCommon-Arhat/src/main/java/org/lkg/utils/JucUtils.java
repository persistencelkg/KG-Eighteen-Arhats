package org.lkg.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Description: JUC 操作便捷工具类
 * Author: 李开广
 * Date: 2025/3/3 9:54 PM
 */
@Slf4j
public class JucUtils {

    public static void repeatCountDownLatch(int n, Runnable runnable) {
        repeatCountDownLatch(n, Integer.MAX_VALUE, runnable);
    }

    public static void repeatCountDownLatch(int n, int seconds, Runnable runnable) {
        CountDownLatch countDownLatch = new CountDownLatch(n);

        Runnable r = () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                countDownLatch.countDown();
            }
        };

        r.run();

        try {
            boolean await = countDownLatch.await(seconds, TimeUnit.SECONDS);
            if (!await) {
                log.warn("execute timeout please mention");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

package org.lkg.retry;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lkg.function.StopAbleConsumer;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/5/15 2:17 PM
 */
@Slf4j
@AllArgsConstructor
public class RetryService<Client, Request>{

    private RetryAble retryAble;
    private Client client;
    private Request request;

    public void retry(BiFunction<Client, Request, Boolean> throwableFunction, StopAbleConsumer stop) {
        retry(throwableFunction, stop, 0);
    }

    public void retry(BiFunction<Client, Request, Boolean> throwableFunction, StopAbleConsumer stop, int count) {
        if (count > retryAble.retryCount()) {
            log.error("[retry service] retry count has surpass the limit {} times, reject execute", retryAble.retryCount());
            return;
        }
        if (stop.stop()) {
            log(count, "retry stop by hand", null);
            return;
        }
        boolean req = retryWithResult(throwableFunction, count);
        if (req) {
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(retryAble.retrySleepMills());
        } catch (InterruptedException ignored) {}
        retry(throwableFunction, stop, ++count);
    }

    private boolean retryWithResult(BiFunction<Client, Request, Boolean> throwableFunction, int count) {
        try {
            return throwableFunction.apply(client, request);
        } catch (Throwable e) {
            log(count, e.getMessage(), e);
        }
        return false;
    }

    private static void log(int count, String msg, Throwable e) {
        log.warn("[retry service]: current retry count:{}, last fail reason:{}", count, msg, e);
    }
}

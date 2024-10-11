package org.lkg.retry;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Description: 通用重试框架
 * Author: 李开广
 * Date: 2024/5/15 2:17 PM
 */
@Slf4j
@AllArgsConstructor
public class RetryService {

    private BulkAsyncRetryAble retryAble;

    @Nullable
    protected  <T> T retryResult(Supplier<T> throwableFunction, Function<T, Boolean> res) {
        return doRetry(throwableFunction, 0, res, false);
    }


    @Nullable
    protected void retryAsync(Consumer<?> consumer, Supplier<Boolean> res) {
        doRetry(() -> {
            consumer.accept(null);
            // 强行给一个假的返回值，实际不会使用，目的复用底层能力
            return false;
        }, 0, (r) -> res.get(), false);
    }

    /**
     * 大多框架本身带有异步重试能力，例如RestHighLevelClient，底层基于ApacheHttpClient
     *
     * @param throwableFunction
     * @param count
     * @param b 是否以throwableFunction作为 res的入参，对于异步回调结果是无返回值的，因此需要设置false
     */
    private  <T> T doRetry(Supplier<T> throwableFunction, int count, Function<T, Boolean> res, boolean b) {
        T t = null;
        if (count > retryAble.count()) {
            log.error("[retry service] retry count has surpass the limit {} times, reject execute", retryAble.count());
            return t;
        }
        if (!retryAble.enable()) {
            log(count, "retry stop by hand", null);
            return t;
        }
        try {
            t = throwableFunction.get();

            // 可能是异常导致用户手动返回null
            if (Objects.isNull(t) || Objects.equals(res.apply(t), Boolean.TRUE) || (!b && res.apply(null))) {
                log.warn("current {}th req fail, ready for retry", count);
                t = doRetry(throwableFunction, ++count, res, false);
            }
        } catch (Throwable e) {
            log(count, e.getMessage(), e);
            if (retryAble.interval() > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(retryAble.interval());
                } catch (InterruptedException ignored) {
                }
            }
            // 失败重试
            t = doRetry(throwableFunction, ++count, res, false);
        }
        return t;

    }

    private static void log(int count, String msg, Throwable e) {
        log.warn("[retry service]: current retry count:{}, last fail reason:{}", count, msg, e);
    }


}

package org.lkg.core.limit;

import io.micrometer.core.instrument.Tag;
import org.lkg.core.config.OnTraceTimeoutEnable;
import org.lkg.core.exception.TraceTimeoutException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lkg.constant.LinkKeyConst;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.Trace;
import org.lkg.core.TraceContext;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.TimerSnapshot;
import org.lkg.enums.TrueFalseEnum;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/17 2:42 PM
 */
@Slf4j
@AllArgsConstructor
public class TraceTimeoutLimiter {

    // 提供自定义trace超时能力
    private static final Map<String, TraceTimeoutLimiter> namespaceLimiterMap = new ConcurrentHashMap<>();
    private static boolean enable;

    static {
        DynamicConfigManger.initAndRegistChangeEvent(OnTraceTimeoutEnable.key, DynamicConfigManger::getInt, (ref) -> {
            enable = TrueFalseEnum.isTrue(ref);
        });
    }


    private String namespace;
    private Tag tag;

    public Long baseCheck(Trace currentContext) {
        if (currentContext == null) {
            return null;
        }
        String extra = currentContext.getExtra(LinkKeyConst.TC_TT);
        try {
            return Long.parseLong(extra);
        } catch (Exception e) {
            log.warn("{} not a valid format timeout", extra);
        }
        return null;
    }

    public long tryCheckAndNextTimeout(long timeout) {
        if (!enable) {
            return timeout;
        }
        Trace currentContext = TraceContext.getCurrentContext();
        Long expectTimeout = baseCheck(currentContext);
        if (Objects.nonNull(expectTimeout)) {
            timeout = Math.min(expectTimeout, timeout);
        }
        long passTime = currentContext.escapeMills();
        timeout -= passTime;
        String format = String.format("current trace time out，expect %s ms return，fail detail: used:%s ms , overload:%s ms", expectTimeout, passTime, Math.abs(timeout));
        if (timeout < 0) {
            TimerSnapshot.clearTimeSnap(namespace, tag);
            throw new TraceTimeoutException(format);
        }
        log.info("trace detail: expect:{} ms used：{} ms, less:{} ms", expectTimeout, passTime, timeout);
        // 检查重试
        currentContext.resetAndStart();
        currentContext.addExtra(LinkKeyConst.TC_TT, String.valueOf(timeout));
        MeterBo meter = TimerSnapshot.getMeter(namespace, tag);
        if (Objects.isNull(meter)) {
            return timeout;
        }
        // 上一次已经消耗完了
        long millis = LongHengMeterRegistry.getInstance().getBaseTimeUnit().toMillis((long) meter.getP999());
        if (millis > timeout) {
            TimerSnapshot.clearTimeSnap(namespace, tag);
            log.info("due retry throw timeout, last use:{} ms", millis);
            throw new TraceTimeoutException(format);
        }
        return timeout;
    }
}

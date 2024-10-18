package org.lkg.core.limit;

import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.lkg.constant.LinkKeyConst;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.Trace;
import org.lkg.core.TraceContext;
import org.lkg.core.config.OnTraceTimeoutEnable;
import org.lkg.core.config.TraceLogEnum;
import org.lkg.core.exception.TraceTimeoutException;
import org.lkg.enums.TrueFalseEnum;
import org.lkg.simple.ObjectUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/17 2:42 PM
 */
@Slf4j
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

    private TraceTimeoutLimiter() {

    }

    public TraceTimeoutLimiter(String namespace, Tag tag) {
        this.namespace = namespace;
        this.tag = tag;
    }

    public static long getAndCheck(String key, long timeOut, TraceLogEnum logEnum) {
        return namespaceLimiterMap.computeIfAbsent(key, ref -> new TraceTimeoutLimiter()).tryCheckAndNextTimeout(timeOut, logEnum);
    }

    /**
     * 针对依赖中间件本身的最大链接时间和超时时间，例如jdbc，redis，es都有这个问题，
     * 当出现耗时问题时 因为这个值往往比业务耗时长，加上上游不断重试，从而占用而浪费资源
     *
     * @param key
     * @return
     */
    public static long getAndCheck(String key, TraceLogEnum logEnum) {
        return getAndCheck(key, -1, logEnum);
    }

    public Long baseCheck(Trace currentContext) {
        if (currentContext == null) {
            return null;
        }
        String extra = currentContext.getExtra(LinkKeyConst.TC_TT);
        if (ObjectUtil.isEmpty(extra)) {
            return null;
        }
        try {
            return Long.parseLong(extra);
        } catch (Exception e) {
            log.warn("{} not a valid format timeout", extra);
        }
        return null;
    }

    public long tryCheckAndNextTimeout(long timeout, TraceLogEnum logEnum) {
        if (!enable) {
            return timeout;
        }
        Trace currentContext = TraceContext.getCurrentContext();
        Long expectTimeout = baseCheck(currentContext);
        if (Objects.nonNull(expectTimeout)) {
            timeout = expectTimeout;
        } else if (timeout < 0) {
            return timeout;
        }
        long passTime = currentContext.escapeMills();
        timeout -= passTime;
        String format = String.format("[%s]:current trace time out，expect %s ms return，cost detail: used:%s ms , overload:%s ms", logEnum.name(), expectTimeout, passTime, Math.abs(timeout));
        if (timeout < 0) {
//            TimerSnapshot.clearTimeSnap(namespace, tag);

            throw new TraceTimeoutException(format);
        }
        log.info("[{}]:trace cost detail: expect:{} ms used：{} ms, less:{} ms, good!", logEnum.name() ,expectTimeout, passTime, timeout);
        // 检查重试
        currentContext.resetAndStart();
        currentContext.addExtra(LinkKeyConst.TC_TT, String.valueOf(timeout));
//        MeterBo meter = TimerSnapshot.getMeter(namespace, tag);
//        if (Objects.isNull(meter)) {
//            return timeout;
//        }
//        // 上一次已经消耗完了
//        long millis = LongHengMeterRegistry.getInstance().getBaseTimeUnit().toMillis((long) meter.getP999());
//        if (millis > timeout) {
//            TimerSnapshot.clearTimeSnap(namespace, tag);
//            log.info("due retry throw timeout, last use:{} ms", millis);
//            throw new TraceTimeoutException(format);
//        }
        return timeout;
    }
}

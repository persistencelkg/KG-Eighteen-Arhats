package org.lkg.core.limit;

import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.lkg.constant.LinkKeyConst;
import org.lkg.core.DynamicConfigManger;
import org.lkg.core.Trace;
import org.lkg.core.TraceContext;
import org.lkg.core.config.EnableTraceTimeOut;
import org.lkg.core.config.TraceLogEnum;
import org.lkg.core.exception.TraceTimeoutException;
import org.lkg.enums.TrueFalseEnum;
import org.lkg.utils.ObjectUtil;

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
    private static final ThreadLocal<TraceTimeoutLimiter> TRACE_TIMEOUT_LIMITER_THREAD_LOCAL = new ThreadLocal<>();
    private static boolean enable;

    static {
        DynamicConfigManger.initAndRegistChangeEvent(EnableTraceTimeOut.key, DynamicConfigManger::getInt, (ref) -> {
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

    private static long defaultCheck(long timeOut, TraceLogEnum logEnum) {
        try {
            TraceTimeoutLimiter traceTimeoutLimiter = new TraceTimeoutLimiter();
            TRACE_TIMEOUT_LIMITER_THREAD_LOCAL.set(traceTimeoutLimiter);

//            return namespaceLimiterMap.computeIfAbsent(key, ref -> new TraceTimeoutLimiter()).tryCheckAndNextTimeout(timeOut, logEnum);
            return traceTimeoutLimiter.tryCheckAndNextTimeout(timeOut, logEnum);
        } finally {
            TRACE_TIMEOUT_LIMITER_THREAD_LOCAL.remove();
        }

    }

    /**
     * 针对依赖中间件本身的最大链接时间和超时时间，例如jdbc，redis，es都有这个问题，
     * 当出现耗时问题时 因为这个值往往比业务耗时长，加上上游不断重试，从而占用而浪费资源
     *
     * @return
     */
    public static long getAndCheck(TraceLogEnum logEnum) {
        return getAndCheck(-1L, logEnum);
    }

    /**
     * 支持二级兜底
     *
     * @param timeOut 取中间件自定义超时时间
     * @param logEnum
     * @return
     */
    public static long getAndCheck(long timeOut, TraceLogEnum logEnum) {
        timeOut = DynamicConfigManger.getLong(logEnum.getTraceTimeOutKey(), timeOut > 0 ? timeOut : -1L);
        return defaultCheck(timeOut, logEnum);
    }

    public static void main(String[] args) {
        System.out.println(TraceLogEnum.ElasticSearch.getTraceTimeOutKey());
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
        // 增加给用户自定义的维度。尤其是对于job、单点调用这样的场景
        if (timeout < 0 && Objects.nonNull(expectTimeout)) {
            timeout = expectTimeout;
        } else if (timeout < 0) {
            return timeout;
        } else {
            expectTimeout = timeout;
        }
        long passTime = currentContext.escapeMills();
        timeout -= passTime;
        String format = String.format("[%s]:current trace time out，expect %s ms return，cost detail: used:%s ms , overload:%s ms", logEnum.name(), expectTimeout, passTime, Math.abs(timeout));
        if (timeout < 0) {
            throw new TraceTimeoutException(format);
        }
        log.info("[{}]:trace cost detail: expect:{} ms used：{} ms, less:{} ms, good!", logEnum.name(), expectTimeout, passTime, timeout);
        // 检查重试
        currentContext.resetAndStart();
        currentContext.addExtra(LinkKeyConst.TC_TT, String.valueOf(timeout));
        return timeout;
    }
}

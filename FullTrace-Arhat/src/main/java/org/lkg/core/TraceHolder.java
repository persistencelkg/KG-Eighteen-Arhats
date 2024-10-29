package org.lkg.core;

import lombok.Getter;
import org.lkg.constant.LinkKeyConst;
import org.lkg.simple.ObjectUtil;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/23 9:43 PM
 */
public final class TraceHolder {

    private List<TraceDecorator> traceDecoratorIterator;

    // 多用于链路发起时
    @Getter
    private ExtraEntryInjector entryInjector;

    private static TraceHolder INSTANCE;

    public static synchronized TraceHolder getInstance(ObjectProvider<TraceDecorator> traceDecoratorObjectProvider, ExtraEntryInjector entryInjector) {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new TraceHolder(traceDecoratorObjectProvider, entryInjector);
        }
        return INSTANCE;
    }

    public static TraceHolder getInstance() {
        return INSTANCE;
    }

    private TraceHolder() {}

    private TraceHolder(ObjectProvider<TraceDecorator> traceDecoratorObjectProvider, ExtraEntryInjector entryInjector) {
        this.traceDecoratorIterator = traceDecoratorObjectProvider.orderedStream().collect(Collectors.toList());
        this.entryInjector = entryInjector;
    }

    public static Trace getCurrent() {
        return TraceContext.getCurrentContext();
    }


    private final static FullLinkPropagation.Setter<Runnable> DEFAUT_SETTER = (carrier, key, val) -> {
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        Runnable runnable = () -> {
            if (ObjectUtil.isNotEmpty(copyOfContextMap)) {
                MDC.setContextMap(copyOfContextMap);
                MDC.put(key, val);
            }
            try {
                carrier.run();
            } finally {
                MDC.clear();
            }
        };

        runnable.run();

    };


    /**
     * 在回调里使用，或者无法从外界获取的场景
     *
     * @return
     */
    public TraceClose newTraceScope() {
        return newTraceScope((FullLinkPropagation.Setter<Object>) null, null);
    }

    /**
     * 在嵌套线程中传递
     *
     * @param runnable
     * @return
     */
    public TraceClose newTraceScope(Runnable runnable) {
        return newTraceScope(DEFAUT_SETTER, runnable);
    }

    // 需要创建传播
    public <Carrier> TraceClose newTraceScope(FullLinkPropagation.Setter<Carrier> setter, Carrier carrier) {
        Trace trace = TraceContext.getCurrentContext();
        if (Objects.isNull(trace)) {
            trace = new Trace();
        }
        if (Objects.nonNull(setter) && Objects.nonNull(carrier)) {
            DefaultPropagation<Carrier> defaultPropagation = new DefaultPropagation<>(setter);
            defaultPropagation.propagation(trace, carrier);
        }

        return newTraceScope(trace);
    }

    public <Carrier> TraceClose newTraceScope(FullLinkPropagation.Getter<Carrier, String> getter, Carrier carrier) {
        String tid = getter.get(carrier, LinkKeyConst.TRACE_ID);
        return newTraceScope(new Trace(tid));
    }

    // 自带传播特性
    public TraceClose newTraceScope(Trace trace) {
        // 携带额外信息
        trace = entryInjector.populateExtra(trace);
        Trace previous = TraceContext.getCurrentContext();
        TraceContext.setContext(trace);
        TraceScope decorator = decorator(trace, () -> {
            TraceContext.remove();
            TraceContext.setContext(previous);
        });
        return new TraceClose(trace, decorator);
    }

    public TraceScope decorator(Trace trace, TraceScope traceScope) {
        for (TraceDecorator traceDecorator : traceDecoratorIterator) {
            traceScope = traceDecorator.decorator(trace, traceScope);
        }
        return traceScope;
    }
}

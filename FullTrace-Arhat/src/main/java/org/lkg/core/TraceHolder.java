package org.lkg.core;

import lombok.Getter;
import org.lkg.constant.LinkKeyConst;
import org.lkg.utils.ObjectUtil;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;

import java.util.*;
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

    public static Trace getCurrentOrCreate() {
        return Optional.ofNullable(TraceContext.getCurrentContext()).orElse(new Trace());
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
        Trace trace = getCurrentOrCreate();
        if (Objects.nonNull(setter) && Objects.nonNull(carrier)) {
            DefaultPropagation<Carrier> defaultPropagation = new DefaultPropagation<>(setter);
            defaultPropagation.propagation(trace, carrier);
        }

        return newTraceScope(trace);
    }

    /**
     * 接收来自组件的traceId
     * @param getter
     * @param carrier
     * @return
     * @param <Carrier>
     */
    public <Carrier> TraceClose newTraceScope(FullLinkPropagation.Getter<Carrier, String> getter, Carrier carrier) {
        if (Objects.isNull(getter) || Objects.isNull(carrier)) {
            return newTraceScope();
        }
        // 按默认全链路透传key
        String tid = getter.get(carrier, LinkKeyConst.getTraceIdKey());
        // 理论这里也可以自动去扩展extra key，为了保证trace的原子性和可维护性，使用者可以通过 TraceExtraHelper 去定制化处理，比较实用extra仅仅存在有限的场景
        return newTraceScope( new Trace(tid));
    }

    // 自带传播特性
    public TraceClose newTraceScope(Trace trace) {
        // 携带自定义key-value的额外信息
        trace = entryInjector.populateExtra(trace);
//        Trace previous = TraceContext.getCurrentContext();
        TraceContext.setContextAfterRemove(trace);

        // 资源释放以lambada形式装饰最后执行
        TraceScope decorator = decorator(trace, () ->{});
        return new TraceClose(trace, decorator);
    }

    public TraceScope decorator(Trace trace, TraceScope traceScope) {
        for (TraceDecorator traceDecorator : traceDecoratorIterator) {
            traceScope = traceDecorator.decorator(trace, traceScope);
        }
        return traceScope;
    }
}

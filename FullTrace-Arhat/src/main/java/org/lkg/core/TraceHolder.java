package org.lkg.core;

import org.lkg.constant.LinkKeyConst;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/23 9:43 PM
 */
public class TraceHolder {

    private final List<TraceDecorator> traceDecoratorIterator;

    // 多用于链路发起时
    private final ExtraEntryInjector entryInjector;

    public TraceHolder(ObjectProvider<TraceDecorator> traceDecoratorObjectProvider, ExtraEntryInjector entryInjector) {
        this.traceDecoratorIterator = traceDecoratorObjectProvider.orderedStream().collect(Collectors.toList());
        this.entryInjector = entryInjector;
    }

    public static boolean existCurrentContext() {
        return Objects.nonNull(TraceContext.getCurrentContext());
    }

    // 需要创建传播
    public <Carrier> TraceClose newTraceScope(FullLinkPropagation.Setter<Carrier> setter, Carrier carrier) {
        Trace trace = TraceContext.getCurrentContext();
        if (Objects.isNull(trace)) {
            trace = entryInjector.populateExtra(new Trace());
        }
        DefaultPropagation<Carrier> defaultPropagation = new DefaultPropagation<>(setter);
        defaultPropagation.propagation(trace, carrier);
        return newTraceScope(trace);
    }

    public <Carrier> TraceClose newTraceScope(FullLinkPropagation.Getter<Carrier, String> getter, Carrier carrier) {
        String tid = getter.get(carrier, LinkKeyConst.TRACE_ID);
        return newTraceScope(new Trace(tid));
    }

    // 自带传播特性
    public TraceClose newTraceScope(Trace trace) {
        Trace previous = TraceContext.getCurrentContext();
        TraceContext.setContext(trace);
        TraceScope decorator = decorator(trace, () -> {
            if (Objects.isNull(previous)) {
                TraceContext.remove();
            } else {
                TraceContext.setContext(previous);
            }
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

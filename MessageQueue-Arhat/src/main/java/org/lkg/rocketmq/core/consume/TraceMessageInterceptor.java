package org.lkg.rocketmq.core.consume;

import lombok.AllArgsConstructor;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/30 10:52 AM
 */
@AllArgsConstructor
public class TraceMessageInterceptor implements ConsumeMessageProcessJoinPointInterceptor{

    private final TraceHolder traceHolder;
    private final static FullLinkPropagation.Getter<MessageExt, String> GETTER = Message::getUserProperty;

    @Override
    public Object intercept(SelfChain selfChain) {
        Object[] args = selfChain.args();
        try (TraceClose traceClose = traceHolder.newTraceScope(GETTER, ((MessageExt) args[0]))) {
            return selfChain.process();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}

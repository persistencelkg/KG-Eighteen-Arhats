package org.lkg.trace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lkg.constant.LinkKeyConst;
import org.lkg.core.FullLinkPropagation;
import org.lkg.core.TraceClose;
import org.lkg.core.TraceHolder;
import org.lkg.metric.api.CommonFilter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/25 8:50 PM
 */
@AllArgsConstructor
@Slf4j
public class CommonTraceFilter implements CommonFilter {

    private final TraceHolder traceHolder;
    private final FullLinkPropagation.Getter<HttpServletRequest, String> GETTER = HttpServletRequest::getHeader;

    @Override
    public void filter(SelfChain selfChain) throws ServletException, IOException {
        try (TraceClose traceClose = traceHolder.newTraceScope(GETTER, selfChain.request())) {
            // 接收上游链路耗时要求
            String expectCostTime = GETTER.get(selfChain.request(), LinkKeyConst.TC_TT);
            traceClose.getTrace().addExtra(LinkKeyConst.TC_TT, expectCostTime);
            if (log.isDebugEnabled()) {
                log.debug("url:{} your upstream expect you should under {} ms return",  selfChain.request().getRequestURI(), expectCostTime);
            }
            selfChain.proceed();
        }

    }

    // 让trace 越早出现越好
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}

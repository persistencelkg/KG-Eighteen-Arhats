package org.lkg.rpc;

import lombok.extern.slf4j.Slf4j;
import org.lkg.constant.LinkKeyConst;
import org.lkg.core.DynamicConfigManger;
import org.lkg.factory.StageContext;
import org.lkg.rpc.lb.LoadBalanceService;
import org.lkg.rpc.lb.ThirdServiceInvokeEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/23 7:29 PM
 */
@Slf4j
public abstract class ProxyTemplate<REQ, RESP> {

    @Autowired(required = false)
    private LoadBalanceService loadBalanceService;

    protected Map<String, String> buildHeader(StageContext stageContext) {
        HashMap<String, String> map = new HashMap<>();
        map.put(LinkKeyConst.TC_TT, "1000");
        return map;
    }

    protected abstract REQ buildRequest(StageContext stageContext);

//    protected abstract ThirdServiceInvokeEnum getThirdInvokeEnum();

    public RESP callResp(StageContext stageContext, ThirdServiceInvokeEnum thirdServiceInvokeEnum) {

        try {
            if (enableFallBack(thirdServiceInvokeEnum)) {
                log.warn("{} enable fallback", thirdServiceInvokeEnum.getDesc());
                return fallBackLogic(stageContext);
            }
            REQ req = buildRequest(stageContext);
            Map<String, String> headers = buildHeader(stageContext);
            return loadBalanceService.post(req, thirdServiceInvokeEnum, headers);
        } catch (Exception e) {
            log.error("invoke {} err", thirdServiceInvokeEnum.getDesc(), e);
        }
        return null;
    }

    protected RESP fallBackLogic(StageContext stageContext) {
        return null;
    }

    protected boolean enableFallBack(ThirdServiceInvokeEnum thirdServiceInvokeEnum) {
        // 服务整体降级
        Boolean serviceFallBack = DynamicConfigManger.getBoolean(thirdServiceInvokeEnum.getServiceFallBackKey());
        if (Boolean.TRUE.equals(serviceFallBack)) {
            return Boolean.TRUE;
        }
        // 单接口降级
        return DynamicConfigManger.getBoolean(thirdServiceInvokeEnum.getFallBackKey(), false);
    }

}

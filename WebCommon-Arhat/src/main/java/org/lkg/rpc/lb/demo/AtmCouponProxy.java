package org.lkg.rpc.lb.demo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.lkg.factory.StageContext;
import org.lkg.rpc.ProxyTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/23 8:25 PM
 */
@Service
public class AtmCouponProxy extends ProxyTemplate<AtmCouponProxy.CouponReq, Map> {

    @Override
    protected CouponReq buildRequest(StageContext stageContext) {
        return new CouponReq(new CouponReq.CouponItemReq("101", 1, "4"));
    }

    @Data
    @AllArgsConstructor
    static class CouponReq {
        private CouponItemReq params;

        @Data
        @AllArgsConstructor
        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        private static class CouponItemReq {
            private String userId;
            private Integer cardType;
            private String clientType;
        }
    }

}

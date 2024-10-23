package org.lkg.rpc.lb;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lkg.enums.StringEnum;
import org.lkg.simple.JacksonUtil;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/22 4:49 PM
 */
@AllArgsConstructor
@Getter
public enum ThirdServiceInvokeEnum {

    COUPON_SELECT_COUPON("atm-coupon","/atm-coupon/user-coupon/validCard/byClient","卡校验", JacksonUtil.getMapReference()),


    ORDER_DETAIL("save-order","/order/detail","订单详情", JacksonUtil.getMapReference());

    ;

    private final String serviceName;
    private final String url;
    private final String desc;
    private final TypeReference<?> typeReference;


    public String getFallBackKey() {
        // atm-coupon.fallback.enable
        return String.join(StringEnum.DOT, serviceName, "fallback", "enable");
    }

    public static void main(String[] args) {
        System.out.println(COUPON_SELECT_COUPON.getFallBackKey());
    }
}

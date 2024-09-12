package org.lkg.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/9 3:24 PM
 */
@FeignClient(
        value = "atm-coupon",
        fallbackFactory = TestCouponFeignFallBack.class,
        path = "${coupon-name:atm-coupon}"
)
public interface TestFeign {
    @RequestMapping(
            value = {"/user-coupon/validCard/byClient"},
            method = {RequestMethod.POST}
    )
    Map<String, Object>  getUserCard(@RequestBody Map<String, Object> bikeReq);

    @GetMapping(
            value = {"/user-coupon/{id}"}
    )
    Map<String, Object>  testId(@RequestBody Map<String, Object> bikeReq);

}


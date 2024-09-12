package org.lkg.feign;

import feign.hystrix.FallbackFactory;
import org.aspectj.weaver.ast.Test;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/10 8:51 PM
 */
@Component
public class TestCouponFeignFallBack implements FallbackFactory<TestFeign> {
    @Override
    public TestFeign create(Throwable throwable) {
        return new TestFeign() {
            @Override
            public Map<String, Object> getUserCard(Map<String, Object> bikeReq) {
                return null;
            }

            @Override
            public Map<String, Object> testId(Map<String, Object> bikeReq) {
                return null;
            }
        };
    }
}

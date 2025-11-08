package org.lkg.structable_pattern.adapter.object_adapter;

import java.math.BigDecimal;

/**
 * @date: 2025/5/25 16:08
 * @author: li kaiguang
 */
public class DefaultLegacyPayment implements LegacyPaymentSystem{
    @Override
    public void oldPay(BigDecimal amount) {
        System.out.println("传统支付：" + amount);
    }
}

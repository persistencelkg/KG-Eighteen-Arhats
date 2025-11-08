package org.lkg.structable_pattern.adapter.object_adapter;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * @date: 2025/5/25 16:08
 * @author: li kaiguang
 */
@AllArgsConstructor
public class PaymentAdapter implements ModernPayment{

    private LegacyPaymentSystem legacyPaymentSystem;

    @Override
    public void pay(String type, BigDecimal amount) {
        System.out.println("新型支付方式：" + type);
        legacyPaymentSystem.oldPay(amount);
        System.out.println("新型支付金额:" + amount);
    }
}

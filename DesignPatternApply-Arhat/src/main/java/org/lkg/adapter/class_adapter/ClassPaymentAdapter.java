package org.lkg.adapter.class_adapter;

import org.lkg.adapter.object_adapter.DefaultLegacyPayment;
import org.lkg.adapter.object_adapter.LegacyPaymentSystem;
import org.lkg.adapter.object_adapter.ModernPayment;

import java.math.BigDecimal;

/**
 * @date: 2025/5/25 16:11
 * @author: li kaiguang
 */
public class ClassPaymentAdapter extends DefaultLegacyPayment implements ModernPayment {
    @Override
    public void pay(String type, BigDecimal amount) {
        System.out.println("新型支付方式:" + type);
        super.oldPay(amount);
        System.out.println("支付金额:" + amount);
    }
}

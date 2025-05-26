package org.lkg;

import org.junit.Test;
import org.lkg.adapter.class_adapter.ClassPaymentAdapter;
import org.lkg.adapter.object_adapter.DefaultLegacyPayment;
import org.lkg.adapter.object_adapter.LegacyPaymentSystem;
import org.lkg.adapter.object_adapter.PaymentAdapter;

import java.math.BigDecimal;

/**
 * @date: 2025/5/25 16:09
 * @author: li kaiguang
 */
public class AdapterTest {

    @Test
    public void testObjectAdapter() {
        DefaultLegacyPayment defaultLegacyPayment = new DefaultLegacyPayment();
        defaultLegacyPayment.oldPay(BigDecimal.TEN);

        System.out.println("----------");
        PaymentAdapter paymentAdapter = new PaymentAdapter(defaultLegacyPayment);
        paymentAdapter.pay("微信支付", BigDecimal.TEN);
    }

    @Test
    public void testClassAdapter() {
        ClassPaymentAdapter classPaymentAdapter = new ClassPaymentAdapter();
        classPaymentAdapter.pay("云闪付", new BigDecimal("22.23"));
    }
}

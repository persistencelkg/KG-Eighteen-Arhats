package org.lkg.adapter.object_adapter;

import java.math.BigDecimal;

/**
 * @date: 2025/5/25 16:05
 * @author: li kaiguang
 * 传统支付方式
 */
public interface LegacyPaymentSystem {

    void oldPay(BigDecimal amount);
}

package org.lkg.adapter.object_adapter;

import java.math.BigDecimal;

/**
 * @date: 2025/5/25 16:04
 * @author: li kaiguang
 * 现代支付接口
 */
public interface ModernPayment {

    void pay(String type, BigDecimal amount);
}

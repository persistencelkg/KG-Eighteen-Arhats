package org.lkg.retry;

import javafx.util.Callback;

/**
 * Description: 具备可重试能力接口
 * 和sprint-retry相比的优势，粒度更细
 * 1. spring-retry 因为aop代理的原因不能再同类调用，所以一旦需要拿到重试代码的结果场景，spring方式就要写更多的代码和class
 * 2. 支持异步重试
 * 3. 运行时动态配置超时次数、时间、间隔等配置
 * Author: 李开广
 * Date: 2024/5/15 11:25 AM
 */
public interface RetryAble {

    String prefix();


    String get(String key);

    // 阶梯重试

}

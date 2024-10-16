package org.lkg.retry;

import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;

/**
 * Description: spring-retry 使用
 * Author: 李开广
 * Date: 2024/10/12 10:56 AM
 */
@Retryable(value = Exception.class)
public class RetryTestService {

    @Recover
    public void test() {
    }
}

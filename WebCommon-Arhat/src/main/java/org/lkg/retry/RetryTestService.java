package org.lkg.retry;

import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/12 10:56 AM
 */
@Retryable(value = Exception.class)
public class RetryTestService {

    public void test() {
    }
}

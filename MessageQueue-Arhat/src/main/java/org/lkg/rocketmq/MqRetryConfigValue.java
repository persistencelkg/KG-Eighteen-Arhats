package org.lkg.rocketmq;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author: 李开广
 * Date: 2023/10/12 2:30 PM
 */
@Component
@Data
public class MqRetryConfigValue {

    @Value("${rocket-mq-retry-count:3")
    private Integer rocketMqRetryCount;


}

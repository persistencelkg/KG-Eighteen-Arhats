package org.lkg.rocketmq.biz;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/29 9:37 PM
 */
@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "test_producer", topic = "test-rocket-topic")
public class MqConsumeService implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt message) {
        log.info("accept rocket mq:{}, data:{}", message.getTopic(), new String(message.getBody(), StandardCharsets.UTF_8));
    }
}

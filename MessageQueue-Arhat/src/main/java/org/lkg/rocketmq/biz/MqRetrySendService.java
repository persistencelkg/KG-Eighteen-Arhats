package org.lkg.rocketmq.biz;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.SystemConfigValue;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Description: 带重试的MQ 保障一定发送消息成功
 * Author: 李开广
 * Date: 2023/10/11 8:46 PM
 */
@Service
@Slf4j
public class MqRetrySendService {

    @Resource
    private MqSendService mqSendService;

    @Resource
    private MqRetryConfigValue mqRetryConfigValue;
    @Resource
    private SystemConfigValue systemConfigValue;
    
    

    

    public void saveToDB(RetryQueueMqPo retryQueueMqPo) {
        // INSERT INTO DB
    }


    public void asyncSendWithRetry(String topic, Object message, DelayLevelEnum level) {
        asyncSendWithRetry(topic, message, level, null);
    }

    public void asyncSendWithRetry(String topic, Object message) {
        asyncSendWithRetry(topic, message, DelayLevelEnum.FIRST_0S, null);
    }


    public void asyncSendWithRetry(String topic,
                                        Object message,
                                        DelayLevelEnum level,
                                        Long timeout
                                        ) {
        sendMessageWithRetry(topic, message, level, timeout, true, mqRetryConfigValue.getRocketMqRetryCount());
    }


    public void sendWithRetry(String topic,
                              Object message
                              ) {
        sendWithRetry(topic, message, null);
    }

    public void sendWithRetry(String topic,
                                             Object message,
                                             Long timeout
                                             ) {
        sendMessageWithRetry(topic, message, null, timeout, false, mqRetryConfigValue.getRocketMqRetryCount());
    }


    private void sendMessageWithRetry(String topic,
                                        Object message,
                                        DelayLevelEnum level,
                                        Long timeout,
                                        boolean async,
                                        int count) {
        if (count > mqRetryConfigValue.getRocketMqRetryCount()) {
            log.error("topic:{} has retry {} times，all send fail，please check", topic, mqRetryConfigValue.getRocketMqRetryCount());
            saveToDB(convertToRetryQueuePo(topic, message, level));
            return;
        }
        boolean res;
        if (async) {
            res = mqSendService.asyncSend(topic, message, level, timeout);
        } else {
            res = mqSendService.syncSendWithTimeOut(topic, message, timeout);
        }
        if (!res) {
            log.warn("topic:{} current retry: {}th", topic, mqRetryConfigValue.getRocketMqRetryCount());
            sendMessageWithRetry(topic, message, level, timeout, async, ++count);
        }
    }

    private RetryQueueMqPo convertToRetryQueuePo(String topic, Object message, DelayLevelEnum level) {
         // 计算下一次延时
        RetryQueueMqPo build = RetryQueueMqPo.builder()
                .topic(topic)
                .message(JacksonUtil.writeValue(message))
                .env(systemConfigValue.getEnv())
                .build();
        build.setNextRunTime(level);
        return build;
    }
}

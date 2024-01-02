package org.lkg.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.SystemConfigValue;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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


    public void asyncSendWithRetry(String topic, Object message, DelayLevelEnum level, String desc) {
        asyncSendWithRetry(topic, message, level, null, desc);
    }

    public void asyncSendWithRetry(String topic, Object message, String desc) {
        asyncSendWithRetry(topic, message, DelayLevelEnum.FIRST_0S, null, desc);
    }


    public void asyncSendWithRetry(String topic,
                                        Object message,
                                        DelayLevelEnum level,
                                        Long timeout,
                                        String desc) {
        sendMessageWithRetry(topic, message, level, timeout, desc, true, mqRetryConfigValue.getRocketMqRetryCount());
    }


    public void sendWithRetry(String topic,
                              Object message,
                              String desc) {
        sendWithRetry(topic, message, null, desc);
    }

    public void sendWithRetry(String topic,
                                             Object message,
                                             Long timeout,
                                             String desc) {
        sendMessageWithRetry(topic, message, null, timeout, desc, false, mqRetryConfigValue.getRocketMqRetryCount());
    }


    private void sendMessageWithRetry(String topic,
                                        Object message,
                                        DelayLevelEnum level,
                                        Long timeout,
                                        String desc,
                                        boolean async,
                                        int count) {
        if (count > mqRetryConfigValue.getRocketMqRetryCount()) {
            log.error("[{}] topic:{} has retry {} times，all send fail，please check", desc, topic, mqRetryConfigValue.getRocketMqRetryCount());
            saveToDB(convertToRetryQueuePo(topic, message, level, desc));
            return;
        }
        boolean res;
        if (async) {
            res = mqSendService.asyncSend(topic, message, level, timeout, desc);
        } else {
            res = mqSendService.sendMessage(topic, message, timeout, desc);
        }
        if (res) {
            log.warn("[{}] topic:{} current retry: {}th", desc, topic, mqRetryConfigValue.getRocketMqRetryCount());
            sendMessageWithRetry(topic, message, level, timeout, desc, async, ++count);
        }
    }

    private RetryQueueMqPo convertToRetryQueuePo(String topic, Object message, DelayLevelEnum level, String desc) {
         // 计算下一次延时
        RetryQueueMqPo build = RetryQueueMqPo.builder()
                .topic(topic)
                .message(JacksonUtil.writeValue(message))
                .desc(desc)
                .env(systemConfigValue.getEnv())
                .build();
        build.setNextRunTime(level);
        return build;
    }
}

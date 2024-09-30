package org.lkg.rocketmq.biz;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.lkg.simple.ObjectUtil;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * RocketMQ 生产者
 *
 */
@Slf4j
@Component
public class MqSendService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource private Map<String, RocketMQTemplate> rocketMQTemplateMap;

    public RocketMQTemplate rocketMQTemplate(String key) {
        if (rocketMQTemplateMap.containsKey(key)) {
            return rocketMQTemplateMap.get(key);
        }
        return rocketMQTemplate;
    }

    /**
     * 简单同步发送
     *
     * @param topic   topic tag发送格式 topic:TAG
     * @param message 消息体
     */
    public void syncSend(String topic, Object message) {
        try {
            this.rocketMQTemplate.convertAndSend(topic, message);
            log.info("sync send message success：topic:{}, message = {}", topic, message);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 发送同步消息
     *
     * @param topic   topic
     * @param message 消息体
     */
    public boolean syncSendWithTimeOut(String topic, Object message, Long timeout) {
        try {
            SendResult sendResult = rocketMQTemplate("primary").syncSend(topic, message,
                    Objects.nonNull(timeout) ? timeout : rocketMQTemplate.getProducer().getSendMsgTimeout());
            log.info("send with timeout:{} success：topic:{}, message:{}, sendResult status:{}",  timeout, topic, message, sendResult.getSendStatus().toString());
            return SendStatus.SEND_OK == sendResult.getSendStatus();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }


    /**
     * 异步发送延时消息
     *
     * @param topic          topic
     * @param message        消息对象
     * @param timeout        超时时间
     * @param delayLevelEnum 延时等级
     */
    public boolean asyncSend(String topic, Object message, DelayLevelEnum delayLevelEnum, Long timeout) {
        final boolean[] result = {true};
        try {
            SendCallback sendCallback = new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("async send delay message success，topic:{}, msgId:{}, message:{}", topic, sendResult.getMsgId(), message);
                }

                @Override
                public void onException(Throwable e) {
                    log.warn("async send delay message error:{}", e.getMessage(), e);
                    result[0] = false;
                }
            };
            rocketMQTemplate("primary").asyncSend(
                    topic,
                    MessageBuilder.withPayload(message).build(),
                    sendCallback,
                    Objects.isNull(timeout) ? rocketMQTemplate.getProducer().getSendMsgTimeout() : timeout,
                    Objects.nonNull(delayLevelEnum.getLevel()) ? delayLevelEnum.getLevel() : 0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result[0] = false;
        }
        return result[0];
    }


    /**
     * 同步发送顺序消息 对标kafka 写入同一个分区
     *
     * @param topic topic
     */
    public boolean syncSendOrderly(String topic, Object payload, String hashKey) {
        SendResult sendResult = null;
        try {
            sendResult = this.rocketMQTemplate.syncSendOrderly(
                    topic,
                    MessageBuilder.withPayload(payload).build(),
                    hashKey);
            log.info("send orderly msg success：topic:{}, msgId:{} message:{}, sendResult:{}", topic, sendResult.getMsgId(), payload, sendResult.getSendStatus().toString());
            return sendResult.getSendStatus() == SendStatus.SEND_OK;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean asyncSendFast(String topic, Object message) {
        return asyncSend(topic, message, DelayLevelEnum.FIRST_0S, null);
    }
}

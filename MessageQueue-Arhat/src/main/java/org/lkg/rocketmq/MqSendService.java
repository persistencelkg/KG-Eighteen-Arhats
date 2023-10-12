package org.lkg.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.lkg.SystemConfigValue;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * RocketMQ 生产者
 */
@Slf4j
@Component
public class MqSendService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 异步发送，不保证可靠交付
     *
     * @param topic   topic
     * @param message 消息体
     */
    public void asyncSendUnsafely(String topic, Object message) {
        try {
            this.rocketMQTemplate.convertAndSend(topic, message);
            log.info("非可靠异步消息发送完成：topic:{}, message = {}", topic, message);
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
    public boolean sendMessage(String topic, Object message, Long timeout, String desc) {
        try {
            SendResult sendResult = this.rocketMQTemplate.syncSend(topic, message,
                    Objects.nonNull(timeout) ? timeout : rocketMQTemplate.getProducer().getSendMsgTimeout());
            log.info("[{}]同步发送消息完成：topic:{}, message:{}, sendResult status:{}", desc, topic, message, sendResult.getSendStatus().toString());
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
    public boolean asyncSend(String topic, Object message, DelayLevelEnum delayLevelEnum, Long timeout, String desc) {
        final boolean[] result = {true};
        try {
            SendCallback sendCallback = new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("[{}]异步-发送延时消息成功，topic:{}, msgId:{}, message:{}", desc, topic, sendResult.getMsgId(), message);
                }

                @Override
                public void onException(Throwable e) {
                    log.warn("[{}]异步发送延时消息发生异常，exception{}", desc, e.getMessage(), e);
                    result[0] = false;
                }
            };
            this.rocketMQTemplate.asyncSend(
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
    public boolean syncSendOrderly(String topic, Object payload, String hashKey, String desc) {
        SendResult sendResult = null;
        try {
            sendResult = this.rocketMQTemplate.syncSendOrderly(
                    topic,
                    MessageBuilder.withPayload(payload).build(),
                    hashKey);
            log.info("{desc}同步顺序发送消息完成：topic:{}, msgId:{} message:{}, sendResult:{}", topic, sendResult.getMsgId(), payload, sendResult.getSendStatus().toString());
            return sendResult.getSendStatus() == SendStatus.SEND_OK;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }



    public boolean syncSend(String topic, Object message, DelayLevelEnum level, Long timeout, String desc) {
        return asyncSend(topic, message, level, timeout, desc);
    }

    public boolean asyncSend(String topic, Object message, DelayLevelEnum level, String desc) {
        return syncSend(topic, message, level, null, desc);
    }

    public boolean asyncSend(String topic, Object message, String desc) {
        return asyncSend(topic, message, DelayLevelEnum.FIRST_0S, null, desc);
    }

    public boolean sendMessage(String topic, Object message, String desc) {
        return sendMessage(topic, message, null, desc);
    }

}

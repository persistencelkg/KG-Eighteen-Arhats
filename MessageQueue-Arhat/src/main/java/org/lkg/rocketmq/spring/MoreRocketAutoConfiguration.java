package org.lkg.rocketmq.spring;

import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.lkg.core.TraceHolder;
import org.lkg.rocketmq.core.MoreRocketMqConfig;
import org.lkg.rocketmq.core.OnEnableMoreRocket;
import org.lkg.simple.RegxUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@OnEnableMoreRocket
@Configuration
public class MoreRocketAutoConfiguration {

    @Resource private TraceHolder traceHolder;

    @Bean(name = MoreRocketMqConfig.primary)
    public RocketMQTemplate primaryRocket(RocketMQMessageConverter rocketMQMessageConverter, MoreRocketMqConfig moreRocketMqConfig) throws MQClientException {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        RocketMQProperties rocketMQProperties = moreRocketMqConfig.getConfig().get(RegxUtil.camelToMinus(MoreRocketMqConfig.primary));
        rocketMQTemplate.setProducer(createProducerWithConfig(RegxUtil.camelToMinus(MoreRocketMqConfig.primary), rocketMQProperties));
        rocketMQTemplate.setConsumer(createConsumerWithConfig(RegxUtil.camelToMinus(MoreRocketMqConfig.primary), rocketMQProperties));
        return rocketMQTemplate;
    }


    @Bean(name = MoreRocketMqConfig.second)
    public RocketMQTemplate secondRocket(RocketMQMessageConverter rocketMQMessageConverter, MoreRocketMqConfig moreRocketMqConfig) throws MQClientException {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        RocketMQProperties rocketMQProperties = moreRocketMqConfig.getConfig().get(RegxUtil.camelToMinus(MoreRocketMqConfig.second));
        rocketMQTemplate.setProducer(createProducerWithConfig(RegxUtil.camelToMinus(MoreRocketMqConfig.second), rocketMQProperties));
        rocketMQTemplate.setConsumer(createConsumerWithConfig(RegxUtil.camelToMinus(MoreRocketMqConfig.second), rocketMQProperties));
        return rocketMQTemplate;
    }

    /**
     * from RocketMQAutoConfiguration
     */
    private DefaultLitePullConsumer createConsumerWithConfig(String primary, RocketMQProperties rocketMQProperties) throws MQClientException {
        RocketMQProperties.Consumer consumerConfig = rocketMQProperties.getConsumer();
        String nameServer = rocketMQProperties.getNameServer();
        String groupName = consumerConfig.getGroup();
        String topicName = consumerConfig.getTopic();
        Assert.hasText(nameServer, "cluster " + primary + " [rocketmq.name-server] must not be null");
        Assert.hasText(groupName, "cluster " + primary + " [rocketmq.consumer.group] must not be null");
        Assert.hasText(topicName, "cluster " + primary + " [rocketmq.consumer.topic] must not be null");

        String accessChannel = rocketMQProperties.getAccessChannel();
        MessageModel messageModel = MessageModel.valueOf(consumerConfig.getMessageModel());
        SelectorType selectorType = SelectorType.valueOf(consumerConfig.getSelectorType());
        String selectorExpression = consumerConfig.getSelectorExpression();
        String ak = consumerConfig.getAccessKey();
        String sk = consumerConfig.getSecretKey();
        int pullBatchSize = consumerConfig.getPullBatchSize();

        DefaultLitePullConsumer litePullConsumer = RocketMQUtil.createDefaultLitePullConsumer(nameServer, accessChannel,
                groupName, topicName, messageModel, selectorType, selectorExpression, ak, sk, pullBatchSize);
        return litePullConsumer;
    }

    /**
     * from RocketMQAutoConfiguration
     */
    private DefaultMQProducer createProducerWithConfig(String key, RocketMQProperties rocketMQProperties) {
        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        String nameServer = rocketMQProperties.getNameServer();
        String groupName = producerConfig.getGroup();
        Assert.hasText(nameServer, "cluster: " + key + " [rocketmq.name-server] must not be null");
        Assert.hasText(groupName, "cluster: " + key + " [rocketmq.producer.group] must not be null");

        String accessChannel = rocketMQProperties.getAccessChannel();

        String ak = rocketMQProperties.getProducer().getAccessKey();
        String sk = rocketMQProperties.getProducer().getSecretKey();
        boolean isEnableMsgTrace = rocketMQProperties.getProducer().isEnableMsgTrace();
        String customizedTraceTopic = rocketMQProperties.getProducer().getCustomizedTraceTopic();

        DefaultMQProducer producer = RocketMQUtil.createDefaultMQProducer(groupName, ak, sk, isEnableMsgTrace, customizedTraceTopic);

        producer.setNamesrvAddr(nameServer);
        if (!StringUtils.isEmpty(accessChannel)) {
            producer.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }
        producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
        return (DefaultMQProducer) ProxyCreateProducerInterceptor.createProducer(producer, traceHolder);
    }
}
package org.lkg.rocketmq.biz;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
public class RetryQueueMqPo {
    private Long id;

    private String topic;

    /**
     * 在mq看是下一个延时级别，在快照上看，是下一次触发的具体时间
     */
    private LocalDateTime nextRunTime;

    private String env;


    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String message;


    public void setNextRunTime(DelayLevelEnum delayLevelEnum) {
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(delayLevelEnum)) {
            return;
        }
        DelayLevelEnum levelEnum = Enum.valueOf(DelayLevelEnum.class, delayLevelEnum.name());
    }
}
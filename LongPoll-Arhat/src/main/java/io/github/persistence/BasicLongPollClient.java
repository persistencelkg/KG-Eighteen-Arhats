package io.github.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.DynamicConfigManger;
import org.lkg.metric.threadpool.TrackableThreadPoolUtil;
import org.lkg.request.InternalRequest;
import org.lkg.request.InternalResponse;
import org.lkg.request.SimpleRequestUtil;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 提供长轮训通用client
 * Description:
 * Author: 李开广
 * Date: 2024/11/1 3:53 PM
 */
@Slf4j
public abstract class BasicLongPollClient {

    protected static final int DEFAULT_LONG_POLL_INTERVAL = 60;

    // 建立和维持服务端实时推送任务的长连接
    private final ScheduledExecutorService remainLongLinkScheduledExecutorService;
    // 负责定期拉取数据
    private ScheduledFuture<?> poolDataScheduledFuture;
    private boolean isLongLinkRunning;

    // 参数
    private int longPollInterval;
    private boolean enableLongPoll;
    @Getter
    @Setter
    private LongPoolConfig longPoolConfig;

    protected BasicLongPollClient(int longPollInterval, boolean enableLongPool, LongPoolConfig longPoolConfig) {
        this(null, longPollInterval, enableLongPool, longPoolConfig);
    }

    protected BasicLongPollClient(String name, int longPollInterval, boolean enableLongPool, LongPoolConfig longPoolConfig) {
        // check
        Assert.isTrue(longPollInterval > 0, "invalid long poll interval config:" + longPollInterval);
        Assert.isTrue(Objects.nonNull(longPoolConfig), "long poll request config can not be null");

        remainLongLinkScheduledExecutorService = TrackableThreadPoolUtil.newTrackScheduledExecutorWithDaemon(Optional.ofNullable(name).orElse("defaultLongPoll"), 2);
        this.longPollInterval = longPollInterval;
        this.enableLongPoll = enableLongPool;
        this.longPoolConfig = longPoolConfig;
        // start poll
        startPollData();
        // create long link
        createAndRemainLongLink();
    }

    private void createAndRemainLongLink() {
        if (!isLongLinkRunning && enableLongPoll) {
            remainLongLinkScheduledExecutorService.execute(this::remainLongLink);
        }
    }

    private void remainLongLink() {
        log.info("long link start");
        this.isLongLinkRunning = true;
        while (enableLongPoll && !Thread.currentThread().isInterrupted()) {
            try {
                // build request
//                InternalRequest.createPostRequest(longPoolConfig.getPollUrl(), InternalRequest.BodyEnum.RAW,  )
                dealWithLongLink(longPoolConfig);
            } catch (Exception ignored) {
                try {
                    TimeUnit.SECONDS.sleep(DynamicConfigManger.getLong("long-poll-fail-retry-interval", 5L));
                } catch (InterruptedException e) {
                    // 重置标记位，告诉while这里被打断过
                    Thread.currentThread().interrupt();
                }
            }
        }
        this.isLongLinkRunning = false;
        log.warn("long link unexpected end");
    }


    private void startPollData() {
        poolDataScheduledFuture = remainLongLinkScheduledExecutorService.schedule(this::startPollData, this.longPollInterval, TimeUnit.SECONDS);
        loadData(longPoolConfig);
    }

    public void refreshInterval(int intervalSecond) {
        // 减少轮询时间就需要打断当前执行中的任务，增大或者等于直接在下一周期自动生效 无需处理
        if (intervalSecond < this.longPollInterval && !poolDataScheduledFuture.isDone() && !poolDataScheduledFuture.isCancelled()) {
            poolDataScheduledFuture.cancel(true);
        }
        this.longPollInterval = intervalSecond;
    }

    public void setLongLinkEnable(boolean enableLongPoll) {
        this.enableLongPoll = enableLongPoll;
        createAndRemainLongLink();
    }

    protected abstract void loadData(LongPoolConfig longPoolConfig);

    protected abstract void dealWithLongLink(LongPoolConfig longPoolConfig);

    protected abstract InternalResponse buildRequest(LongPoolConfig longPoolConfig);


    protected boolean isSuc(int code) {
        return code >= 200 && code < 400;
    }

    protected boolean isFail(int code) {
        return code > 400;
    }


    public static void main(String[] args) throws InterruptedException {
        new BasicLongPollClient(1, true, new LongPoolConfig()) {
            @Override
            protected void loadData(LongPoolConfig longPoolConfig) {
                System.out.println("这是1");
            }

            @Override
            protected void dealWithLongLink(LongPoolConfig longPoolConfig) {
//                System.out.println(222);
            }

            @Override
            protected InternalResponse buildRequest(LongPoolConfig longPoolConfig) {
                return null;
            }
        };

//        LockSupport.park();

    }
}
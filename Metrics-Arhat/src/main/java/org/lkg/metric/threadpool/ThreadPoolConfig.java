package org.lkg.metric.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.lkg.utils.ObjectUtil;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池
 *
 * @author likaiguang
 * @date 2023/2/12 8:48 下午
 */
@Configuration
@Slf4j
public class ThreadPoolConfig {


    public static class MdcTaskDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
            if (ObjectUtil.isEmpty(copyOfContextMap)) {
                return runnable;
            }
            return () -> {
                try {
                    MDC.setContextMap(copyOfContextMap);
                    runnable.run();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    MDC.clear();
                }
            };
        }
    }


    interface SelfExecutorService {
        /**
         * @return @link SelfExecutorService
         */
        ExecutorService create(String prefix, int queueSize, RejectedExecutionHandler rejectedExecutionHandler);

        /**
         * 创建默认调用者的线程
         *
         * @param prefix
         * @param queueSize
         * @return
         */
        ExecutorService defaultCreate(String prefix, int queueSize);
    }

    @Bean
    public SelfExecutorService selfExecutorService() {
        return new SelfExecutorService() {
            @Override
            public ExecutorService create(String prefix, int queueSize, RejectedExecutionHandler rejectedExecutionHandler) {
                return TrackableThreadPoolUtil.newTrackableExecutor(prefix, queueSize, rejectedExecutionHandler);
            }

            @Override
            public ExecutorService defaultCreate(String prefix, int queueSize) {
                return create(prefix, queueSize, new ThreadPoolExecutor.CallerRunsPolicy());
            }
        };
    }

}

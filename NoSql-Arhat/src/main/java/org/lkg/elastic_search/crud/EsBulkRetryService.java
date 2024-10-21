package org.lkg.elastic_search.crud;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.lkg.core.config.TraceLogEnum;
import org.lkg.core.limit.TraceTimeoutLimiter;
import org.lkg.retry.BulkAsyncRetryAble;
import org.lkg.retry.RetryAble;
import org.lkg.retry.RetryInterceptor;
import org.lkg.retry.RetryService;

import java.io.IOException;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/5/15 3:01 PM
 */

//@Service
@Slf4j
public class EsBulkRetryService extends RetryService {

    public EsBulkRetryService(BulkAsyncRetryAble retryAble) {
        super(retryAble, (ref) -> TraceTimeoutLimiter.getAndCheck(TraceLogEnum.ElasticSearch));
    }

    private void retryASync(RestHighLevelClient client, BulkRequest bulkRequest) throws Throwable {
        final boolean[] failure = {false};
        ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkItemResponses) {
                // 处理失败信息 某个失败
                if (bulkItemResponses.hasFailures()) {
                    failure[0] = true;
                    logFailResponse(bulkItemResponses);
                }
            }

            /**
             * 整体失败进行重试
             * @param e
             */
            @Override
            public void onFailure(Exception e) {
                log.error("all es request fail: {} ready retry", e.getMessage());
                failure[0] = true;
            }

        };
        retryAsync((res) -> client.bulkAsync(bulkRequest, RequestOptions.DEFAULT, listener),  () -> failure[0]);
    }

    private void retry(RestHighLevelClient client, BulkRequest bulkRequest) throws Throwable {
        retryResult(() -> {
            try {
                return client.bulk(bulkRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }, true);
    }


    /**
     * 打印具体失败响应
     *
     * @param bulkItemResponses
     */
    private void logFailResponse(BulkResponse bulkItemResponses) {
        BulkItemResponse[] items = bulkItemResponses.getItems();
        for (BulkItemResponse bulkResponse : items) {
            if (bulkResponse.isFailed()) {
                log.error("bulk request has part failure: index: {}, reason:{}", bulkResponse.getIndex(), bulkResponse.getFailureMessage());
            }
        }
    }

}

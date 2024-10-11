package org.lkg.elastic_search.crud;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MetaDataIndexTemplateService;
import org.elasticsearch.rest.RestStatus;
import org.lkg.core.DynamicConfigManger;
import org.lkg.elastic_search.enums.EsDoc;
import org.lkg.retry.BulkAsyncRetryAble;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Description: 抽象的ESAPI使用，基于HashMap作为元数据
 * Author: 李开广
 * Date: 2024/7/5 4:34 PM
 */
@Slf4j
@Service
public class MapDataEsApIService extends EsBulkRetryService implements EsApIService<Map<String, Object>> {

    public MapDataEsApIService(BulkAsyncRetryAble esRetryAble) {
        super(esRetryAble);
    }

    @Override
    public boolean saveOrUpdateDocument(RestHighLevelClient client, String index, String type, String id, Map<String, Object> obj) {
        if (ObjectUtil.isEmpty(obj)) {
            log.warn("no data to save or update");
            return false;
        }
        UpdateRequest request = new UpdateRequest(index, type, id);
        request.doc(obj);
        request.docAsUpsert(true);
        try {
            UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
            return update.getShardInfo().getSuccessful() > 0;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deleteDocument(RestHighLevelClient client, Map<String, Object> obj) {
        return false;
    }

    @Override
    public void batchUpdateDocument(RestHighLevelClient client, Collection<?> collection, boolean async) {
        if (ObjectUtil.isEmpty(collection)) {
            return;
        }
        ArrayList<?> list = new ArrayList<>(collection);
        Object template = list.get(0);
        String indexName = ObjectUtil.camelToUnderline(template.getClass().getSimpleName());
        EsDoc annotation = template.getClass().getAnnotation(EsDoc.class);
        if (Objects.isNull(annotation)) {
            log.error("{} not config annotation 'EsDoc'", template.getClass().getSimpleName());
            return;
        }
        String esType = annotation.type();

        BulkRequest bulkRequest = new BulkRequest();
        for (Object o : collection) {
            try {
                Field declaredField = o.getClass().getDeclaredField(annotation.uniqueKey());
                Object uniqueValue = declaredField.get(o);
                IndexRequest indexRequest = new IndexRequest(indexName, esType, uniqueValue.toString());
                indexRequest.source(JacksonUtil.writeValue(o));
                bulkRequest.add(indexRequest);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("invalid unique key:{} for es", annotation.uniqueKey());
                return;
            }
        }
        // 发起请求
        try {
            BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.info("batch update res:{} took:{}ms", bulk.hasFailures(), bulk.getTook().getMillis());
            if (bulk.hasFailures()) {
                log.error(bulk.buildFailureMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void batchDeleteDocument(RestHighLevelClient client, Collection<?> collection, boolean async) {

    }

    @Override
    public Map<String, Object> getDocument(RestHighLevelClient client, String index, String type, String id) {
        GetRequest getIndexRequest = new GetRequest(index, type, id);
        GetResponse documentFields = retryResult(() -> {
            try {
                return client.get(getIndexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }, ref -> !ref.isExists());
        if (Objects.nonNull(documentFields)) {
            return documentFields.getSourceAsMap();
        }
        return null;
    }


    @Override
    public List<Map<String, Object>> multiGetDocument(RestHighLevelClient client, String index, String type, Collection<String> ids) {
        return null;
    }

    @Override
    public List<Map<String, Object>> listDocumentWithCondition(RestHighLevelClient client, QueryContext queryContext) {
        return null;
    }


}

package org.lkg.elastic_search.crud;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MetaDataIndexTemplateService;
import org.elasticsearch.rest.RestStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description: 抽象的ESAPI使用，基于HashMap作为元数据
 * Author: 李开广
 * Date: 2024/7/5 4:34 PM
 */
@Slf4j
@Service
public class MapDataEsApIService implements EsApIService<Map<String, Object>> {

    @Override
    public boolean saveOrUpdateDocument(RestHighLevelClient client, String index, String type, String id, Map<String, Object> obj) {
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

    }

    @Override
    public void batchDeleteDocument(RestHighLevelClient client, Collection<?> collection, boolean async) {

    }

    @Override
    public Map<String, Object> getDocument(RestHighLevelClient client, String index, String type, String id) {
        GetRequest getIndexRequest = new GetRequest(index, type, id);
        try {
            GetResponse getResponse = client.get(getIndexRequest, RequestOptions.DEFAULT);
            return getResponse.getSourceAsMap();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
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

package org.lkg.elastic_search.crud;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 抽象的ESAPI使用，基于HashMap作为元数据
 * Author: 李开广
 * Date: 2024/7/5 4:34 PM
 */
public class MapDataEsApIService implements EsApIService<Map<String, Object>>{

    @Override
    public boolean createDocumentIfAbsent(RestHighLevelClient client, Map<String, Object> obj) {

//        PutIndexTemplateRequest
//        IndexRequest indexRequest = new IndexRequest();
        return false;
    }

    @Override
    public boolean updateDocument(RestHighLevelClient client, Map<String, Object> obj) {
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

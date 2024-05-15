package org.lkg.elastic_search.crud;

import org.elasticsearch.client.RestHighLevelClient;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/5/14 8:34 PM
 */
public interface EsMetaApIService {

    boolean createIndex(RestHighLevelClient client, String indexName, String type);

    boolean updateTemplate(RestHighLevelClient client, String indexPrefix, String type, Map<String, Object> mapping);

    boolean dropIndex(RestHighLevelClient client, String index);

}

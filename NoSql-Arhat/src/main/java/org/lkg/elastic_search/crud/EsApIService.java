package org.lkg.elastic_search.crud;

import org.elasticsearch.client.RestHighLevelClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: ES Index API、Update API、GET API、Update/Delete By Query API
 * Author: 李开广
 * Date: 2024/5/14 8:25 PM
 */
public interface EsApIService<T> {

    boolean createDocumentIfAbsent(RestHighLevelClient client, T obj);

    boolean updateDocument(RestHighLevelClient client, T obj);

    boolean deleteDocument(RestHighLevelClient client, T obj);

    void batchUpdateDocument(RestHighLevelClient client, Collection<?> collection, boolean async);

    void batchDeleteDocument(RestHighLevelClient client, Collection<?> collection, boolean async);


    T getDocument(RestHighLevelClient client, String index, String type, String id);

    List<T> multiGetDocument(RestHighLevelClient client, String index, String type, Collection<String> ids);

    List<T> listDocumentWithCondition(RestHighLevelClient client, QueryContext queryContext);


}

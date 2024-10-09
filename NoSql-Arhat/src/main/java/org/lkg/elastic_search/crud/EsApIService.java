package org.lkg.elastic_search.crud;

import org.elasticsearch.client.RestHighLevelClient;

import java.util.Collection;
import java.util.List;

/**
 * Description: ES Index API、Update API、GET API、Update/Delete By Query API
 * Author: 李开广
 * Date: 2024/5/14 8:25 PM
 */
public interface EsApIService<T> {

    boolean saveOrUpdateDocument(RestHighLevelClient client, String index, String type, String id, T obj);

    default boolean saveOrUpdateDocument(RestHighLevelClient client, String index, String id, T obj) {
        return saveOrUpdateDocument(client, index, EsMetaApIService.DEFAULT_TYPE, id, obj);
    }
    boolean deleteDocument(RestHighLevelClient client, T obj);

    void batchUpdateDocument(RestHighLevelClient client, Collection<?> collection, boolean async);

    void batchDeleteDocument(RestHighLevelClient client, Collection<?> collection, boolean async);


    T getDocument(RestHighLevelClient client, String index, String type, String id);

    default T getDocument(RestHighLevelClient client, String index, String id){
        return getDocument(client, index, EsMetaApIService.DEFAULT_TYPE, id);
    }

    List<T> multiGetDocument(RestHighLevelClient client, String index, String type, Collection<String> ids);

    List<T> listDocumentWithCondition(RestHighLevelClient client, QueryContext queryContext);


}

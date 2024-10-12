package org.lkg.elastic_search.crud;

import org.elasticsearch.action.DocWriteRequest;
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


    boolean saveOrUpdateDocument(RestHighLevelClient client, String id, T obj);

    boolean deleteDocument(RestHighLevelClient client, T obj);

    default void batchSaveOrUpdateDocument(RestHighLevelClient client, Collection<T> collection, boolean async) {
        batchUpdateDocument(client, collection, async, DocWriteRequest.OpType.INDEX);
    }

    default void bachDeleteDocument(RestHighLevelClient client, Collection<T> collection, boolean async) {
        batchUpdateDocument(client, collection, async, DocWriteRequest.OpType.DELETE);
    }

    void batchUpdateDocument(RestHighLevelClient client, Collection<T> collection, boolean async, DocWriteRequest.OpType opType);


    T getDocument(RestHighLevelClient client, Class<T> tClass, String id);

    Map<String, Object> getDocumentMap(RestHighLevelClient client, Class<T> tClass, String id);

    List<T> multiGetDocument(RestHighLevelClient client, Class<T> tClass, Collection<String> ids);

    List<T> listDocumentWithCondition(RestHighLevelClient client, QueryContext queryContext);


}

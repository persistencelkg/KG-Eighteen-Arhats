package org.lkg.elastic_search.crud;

import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.lkg.request.PageResponse;
import org.lkg.simple.JacksonUtil;

import java.util.*;
import java.util.stream.Collectors;

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

    SearchResponse listOriginDocumentWithCondition(RestHighLevelClient client, Class<T> tClass, QueryContext queryContext);

    default List<T> listDocumentWithCondition(RestHighLevelClient client, Class<T> tClass, QueryContext queryContext) {
        SearchResponse searchResponse = listOriginDocumentWithCondition(client, tClass, queryContext);
        if (Objects.isNull(searchResponse)) {
            return new ArrayList<>();
        }
        return Arrays.stream(searchResponse.getHits().getHits()).map(ref -> JacksonUtil.readValue(ref.getSourceAsString(), tClass)).collect(Collectors.toList());

    }

    default PageResponse<T> listDocumentWithPage(RestHighLevelClient client, Class<T> tClass, QueryContext queryContext) {
        SearchResponse searchResponse = listOriginDocumentWithCondition(client, tClass, queryContext);
        if (Objects.isNull(searchResponse)) {
            return PageResponse.emptyPage(queryContext.getPageRequest());
        }
        List<T> collect = Arrays.stream(searchResponse.getHits().getHits()).map(ref -> JacksonUtil.readValue(ref.getSourceAsString(), tClass)).collect(Collectors.toList());
        return PageResponse.safePage(collect, Math.toIntExact(searchResponse.getHits().getTotalHits()), queryContext.getPageRequest());
    }
}

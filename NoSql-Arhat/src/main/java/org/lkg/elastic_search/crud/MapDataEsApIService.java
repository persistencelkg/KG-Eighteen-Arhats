package org.lkg.elastic_search.crud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteAction;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.lkg.elastic_search.enums.EsDoc;
import org.lkg.retry.BulkAsyncRetryAble;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 抽象的ESAPI使用，基于HashMap作为元数据
 * Author: 李开广
 * Date: 2024/7/5 4:34 PM
 */
@Slf4j
@Service
public class MapDataEsApIService<T> extends EsBulkRetryService implements EsApIService<T> {

    public MapDataEsApIService(BulkAsyncRetryAble esRetryAble) {
        super(esRetryAble);
    }

    @Override
    public boolean saveOrUpdateDocument(RestHighLevelClient client, String id, T obj) {
        EsContext esContext = returnIfValid(obj);
        UpdateRequest request = new UpdateRequest(esContext.getIndex(), esContext.getType(), id);
        request.doc(JacksonUtil.writeValue(obj), XContentType.JSON);
        request.docAsUpsert(true);
//        request.fetchSource(true) 是否操作后返回新数据 update 操作特有
        try {
            UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
            return update.getShardInfo().getSuccessful() > 0;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deleteDocument(RestHighLevelClient client, T obj) {
        EsContext esContext = returnIfValid(obj);
        try {
            Object uniqueValue = getUniqueValue(obj, esContext);
            DeleteRequest deleteRequest = new DeleteRequest(esContext.getIndex(), esContext.getType(), uniqueValue.toString());
            client.deleteAsync(deleteRequest, RequestOptions.DEFAULT, new ActionListener<DeleteResponse>() {
                @Override
                public void onResponse(DeleteResponse deleteResponse) {
                    log.info("{} delete success", esContext.getIndex());
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("{} delete fail :{}", esContext.getIndex(), e.getCause(), e);
                }
            });
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getUniqueValue(T obj, EsContext esContext) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = obj.getClass().getDeclaredField(esContext.getUniqueKey());
        declaredField.setAccessible(true);
        Object o = declaredField.get(obj);
        if (Objects.isNull(o)) {
            throw new IllegalArgumentException(esContext.getUniqueKey() + " not a valid format for '_id' ");
        }
        declaredField.setAccessible(false);
        return o;
    }

    @Override
    public void batchUpdateDocument(RestHighLevelClient client, Collection<T> collection, boolean async, DocWriteRequest.OpType opType) {
        if (ObjectUtil.isEmpty(collection)) {
            return;
        }
        T template = collection.stream().findFirst().get();
        EsContext esContext = returnIfValid(template);

        BulkRequest bulkRequest = new BulkRequest();
        for (T o : collection) {
            try {
                Object uniqueValue = getUniqueValue(o, esContext);
                if (Objects.equals(opType, DocWriteRequest.OpType.DELETE)) {
                    DeleteRequest indexRequest = new DeleteRequest(esContext.getIndex(), esContext.getType(), uniqueValue.toString());
                    bulkRequest.add(indexRequest);
                // 按需更新，注意不要让序列化策略能序列空值，如果携带了和IndexRequest一致，本意还是按需更新，既然你要设置null 那就没办法了
                } else if (Objects.equals(opType, DocWriteRequest.OpType.UPDATE)) {
                    UpdateRequest request = new UpdateRequest(esContext.getIndex(), esContext.getType(), uniqueValue.toString());
                    request.doc(JacksonUtil.writeValue(o), XContentType.JSON);
                    request.docAsUpsert(true);
                    bulkRequest.add(request);
                // 完全覆盖
                } else {
                    IndexRequest indexRequest = new IndexRequest(esContext.getIndex(), esContext.getType(), uniqueValue.toString());
                    indexRequest.source(JacksonUtil.writeValue(o), XContentType.JSON);
                    bulkRequest.add(indexRequest);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("invalid unique key:{} for es", esContext.getUniqueKey());
                return;
            }
        }
        if (async) {
            client.bulkAsync(bulkRequest, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
                @Override
                public void onResponse(BulkResponse bulk) {
                    log.info("batch update res:{} took:{}ms", bulk.hasFailures(), bulk.getTook().getMillis());
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("batch update fail:{}",e.getMessage(), e);
                }
            });
        } else {
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

    }

    private EsContext returnIfValid(T template) {
        Assert.notNull(template, "es data not empty");
        return returnIfValid(template.getClass());
    }

    private EsContext returnIfValid(Class<?> template) {
        Assert.notNull(template, "es class not empty");
        EsDoc annotation = template.getAnnotation(EsDoc.class);
        String index = template.getSimpleName();
        Assert.notNull(annotation, index + " not config annotation 'EsDoc'");
        return new EsContext(ObjectUtil.camelToUnderline(index), annotation.type(), annotation.uniqueKey());
    }

    @Override
    public T getDocument(RestHighLevelClient client, Class<T> tClass, String id) {
        GetResponse documentFields = getResponse(client, tClass, id);
        if (Objects.nonNull(documentFields)) {
            return JacksonUtil.readValue(new String(documentFields.getSourceAsBytes(), StandardCharsets.UTF_8), tClass);
        }
        return null;
    }

    @Override
    public Map<String, Object> getDocumentMap(RestHighLevelClient client, Class<T> tClass, String id) {

        GetResponse documentFields = getResponse(client, tClass, id);
        if (Objects.nonNull(documentFields)) {
            return documentFields.getSourceAsMap();
        }
        return null;

    }

    private GetResponse getResponse(RestHighLevelClient client, Class<T> tClass, String id){
        EsContext esContext = returnIfValid(tClass);
        GetRequest getIndexRequest = new GetRequest(esContext.getIndex(), esContext.getType(), id);
        return retryResult(() -> {
            try {
                return client.get(getIndexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, true);
    }

    @Override
    public List<T> multiGetDocument(RestHighLevelClient client, Class<T> tClass, Collection<String> ids) {
        EsContext esContext = returnIfValid(tClass);
        MultiGetRequest items = new MultiGetRequest();
        ids.forEach( ref -> items.add(esContext.getIndex(), esContext.getType(), ref));
        try {
            log.info("multi get search es req:{}", items);
            MultiGetResponse mget = client.mget(items, RequestOptions.DEFAULT);
            MultiGetItemResponse[] responses = mget.getResponses();
            if (ObjectUtil.isEmpty(responses)) {
                return null;
            }
            return Arrays.stream(responses)
                    .map(ref -> JacksonUtil.readValue(ref.getResponse().getSourceAsString(), tClass))
                    .filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> listDocumentWithCondition(RestHighLevelClient client, Class<T> tClass, QueryContext queryContext) {
        Assert.isTrue(Objects.nonNull(queryContext), "query context not empty");
        SearchRequest searchRequest = QueryContext.buildSearchRequest(queryContext);
        log.info("base query condition search es req:{}", searchRequest.source());
        SearchResponse searchResponse = retryResult(() -> {
            try {
                return client.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }, true);
        if (Objects.isNull(searchResponse)) {
            return new ArrayList<>();
        }
        return Arrays.stream(searchResponse.getHits().getHits()).map(ref -> JacksonUtil.readValue(ref.getSourceAsString(), tClass)).collect(Collectors.toList());
    }


    @Data
    @AllArgsConstructor
    static class EsContext {
        private String index;
        private String type;
        private String uniqueKey;
    }
}

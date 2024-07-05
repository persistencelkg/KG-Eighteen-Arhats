package org.lkg.elastic_search.crud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.cert.ocsp.Req;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.lkg.simple.ObjectUtil;

import java.util.Collection;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/5/14 8:55 PM
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
//@Component
public class QueryContext {

    private int size = 500;
    private int retrySize = 3;
    private int sleepMillSecond = 500;
    private String indexName;
    private String indexType;
    private String[] fetchColumns;
    private QueryBuilder queryBuilder;
    /**
     * 合理的排序字段，建议是有序的id或者时间
     */
    private String sortField;
    /**
     * tip：queryBuilder对数据的范围查询 不要过大， 否则es深度分页也会有问题
     * 因为es默认每次查询10条，最大支持查询10000条 一个连接情况下
     */
    private SortOrder sortOrder = SortOrder.ASC;

    public static QueryContext build(String indexName, String indexType, QueryBuilder queryBuilder) {
        return QueryContext.builder().indexName(indexName).indexType(indexType).queryBuilder(queryBuilder).build();
    }

    public static SearchRequest buildSearchRequest(QueryContext context) {
        if (ObjectUtil.isEmpty(context)) {
            return new SearchRequest();
        }
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource()
                .query(context.getQueryBuilder())
                .size(context.getSize());
        if (!ObjectUtil.isEmpty(context.getSortField())) {
            sourceBuilder.sort(context.getSortField(), context.getSortOrder());
        }
        if (!ObjectUtil.isEmpty(context.getFetchColumns())) {
            sourceBuilder.fetchSource(context.getFetchColumns(), null);
        }
        return Requests.searchRequest()
                .indices(context.getIndexName())
                .source(sourceBuilder)
                .indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
    }


    public static void main(String[] args) {
//        QueryContext queryContext = new QueryContext("11", "22", QueryBuilders.boolQuery());
        QueryContext build = QueryContext.build("11", "22", QueryBuilders.boolQuery());
        System.out.println(build);
    }


}

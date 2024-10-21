package org.lkg.elastic_search.crud;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.lkg.elastic_search.crud.demo.Orders;
import org.lkg.elastic_search.enums.EsDoc;
import org.lkg.request.PageRequest;
import org.lkg.simple.ObjectUtil;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/5/14 8:55 PM
 */

@Data
@AllArgsConstructor
public class QueryContext {
    /**
     * tip：queryBuilder对数据的范围查询 不要过大， 否则es深度分页也会有问题
     * 因为es默认每次查询10条，最大支持查询10000条 一个连接情况下
     */
    private int size = 10;
    private String indexName;
    private String indexType;
    private String[] fetchColumns;
    private QueryBuilder queryBuilder;
    /**
     * 合理的排序字段，建议是有序的id或者时间
     */
    private PageRequest pageRequest;

    private boolean paging;

    private QueryContext() {

    }

    public static class Builder {
        private String indexName;
        private String type;
        private String uniqueKey;
        private String[] fetchColumn;
        private int size = 10;
        private QueryBuilder queryBuilder;
        private PageRequest pageRequest;

        private <T> Builder(Class<T> tClass) {
            Assert.isTrue(Objects.nonNull(tClass) && tClass.isAnnotationPresent(EsDoc.class), "QueryContext only for es doc obj to build");
            EsDoc annotation = tClass.getAnnotation(EsDoc.class);
            this.indexName = ObjectUtil.camelToUnderline(tClass.getSimpleName());
            this.type = annotation.type();
            this.uniqueKey = annotation.uniqueKey();
        }

        public static <T> Builder builder(Class<T> tClass) {
            return new Builder(tClass);
        }

        public Builder fetchColumn(String... fetchColumn) {
            this.fetchColumn = fetchColumn;
            return this;
        }

        public Builder size(int size) {
            Assert.isTrue(size < 10000 && size > 0, "too large much size overload 10000");
            this.size = size;
            return this;
        }


        public Builder queryBuilder(QueryBuilder queryBuilder) {
            return queryBuilder(true, queryBuilder);
        }

        public Builder queryBuilder(boolean withoutScore, QueryBuilder queryBuilder) {
            // 减少倒排索引因为排序等耗时聚合计算操作，有助于缓存相同的查询
            if (withoutScore) {
                this.queryBuilder = QueryBuilders.constantScoreQuery(queryBuilder);
            } else {
                this.queryBuilder = queryBuilder;
            }
            return this;
        }

        public Builder pageRequest(PageRequest pageRequest) {
            this.pageRequest = pageRequest;
            return this;
        }

        public QueryContext build() {
            return new QueryContext(size, indexName, type, fetchColumn, queryBuilder, pageRequest, false);
        }
    }


    public static <T> Builder newBuilder(Class<T> tClass, QueryBuilder queryBuilder) {
        return Builder.builder(tClass).queryBuilder(queryBuilder);
    }

    public static SearchRequest buildSearchRequest(QueryContext context) {
        if (ObjectUtil.isEmpty(context)) {
            return new SearchRequest();
        }
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource()
                .query(context.getQueryBuilder())
                .size(context.getSize());
        PageRequest pageRequest = context.getPageRequest();
        if (!ObjectUtil.isEmpty(pageRequest)) {
            sourceBuilder.from((pageRequest.getPageIndex() - 1) * pageRequest.getPageSize());
            sourceBuilder.size(pageRequest.getPageSize());
            context.setPaging(true);
            if (ObjectUtil.isNotEmpty(pageRequest.getSortOrderContext())) {
                Arrays.stream(pageRequest.getSortOrderContext()).forEach(ref -> {
                    sourceBuilder.sort(ref.getField(), SortOrder.fromString(ref.getSortOrderEnum().getOrder()));
                });
            }
        }
        if (!ObjectUtil.isEmpty(context.getFetchColumns())) {
            sourceBuilder.fetchSource(context.getFetchColumns(), null);
        }
        return Requests.searchRequest()
                .indices(context.getIndexName())
                .source(sourceBuilder)
                .indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
    }

    @Data
    public static class SortContext {
        private String field;
        private SortOrder sortOrder;
        private PageRequest pageRequest;

        public SortContext(String field, SortOrder orders) {
            this(field, orders, null);
        }

        public SortContext(String field, SortOrder orders, PageRequest pageRequest) {
            Assert.notNull(field, "sort field not null");
            Assert.notNull(orders, "sort rule not null");
            this.field = field;
            this.sortOrder = orders;
            if (Objects.nonNull(pageRequest)) {
                Assert.isTrue(pageRequest.getPageIndex() > 0, "pageIndex invalid");
                Assert.isTrue(pageRequest.getPageSize() > 0 && pageRequest.getPageSize() <= PageRequest.MAX_PAGE_SIZE, "pageSize invalid");
                this.pageRequest = pageRequest;
            }
        }
    }


}

package org.lkg.elastic_search.spring;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.lkg.simple.ObjectUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/22 1:44 PM
 */
@Aspect
//@Component
@Slf4j
public class EsClientAspecj {


    @Around("execution(public * org.elasticsearch.client.RestHighLevelClient.search(..)))")
    public Object pjp(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        if (ObjectUtil.isEmpty(args) || args.length < 1 || !(args[0] instanceof SearchRequest)) {
            return proceedingJoinPoint.proceed();
        }

        Object arg = args[1];
        SearchRequest searchRequest = (SearchRequest) arg;
        long start = System.nanoTime();
        boolean res;
        String s = esQueryToSQL(searchRequest);
        log.info("es dsl:{}", s);
        return proceedingJoinPoint.proceed();


    }

    public String esQueryToSQL(SearchRequest searchRequest) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        // 提取索引名称
        sql.append(searchRequest.indices()[0]).append(" WHERE ");

        // 提取查询部分（例如 BoolQueryBuilder）
        QueryBuilder query = searchRequest.source().query();
        if (query instanceof BoolQueryBuilder) {
            BoolQueryBuilder boolQuery = (BoolQueryBuilder) query;
            List<QueryBuilder> mustClauses = boolQuery.must();

            for (int i = 0; i < mustClauses.size(); i++) {
                QueryBuilder clause = mustClauses.get(i);
                if (clause instanceof TermQueryBuilder) {
                    TermQueryBuilder termQuery = (TermQueryBuilder) clause;
                    sql.append(termQuery.fieldName()).append(" = '")
                            .append(termQuery.value()).append("'");
                } else if (clause instanceof RangeQueryBuilder) {
                    RangeQueryBuilder rangeQuery = (RangeQueryBuilder) clause;
                    sql.append(rangeQuery.fieldName()).append(" >= ")
                            .append(rangeQuery.from());
                }
                if (i < mustClauses.size() - 1) {
                    sql.append(" AND ");
                }
            }
        }
        return sql.toString();
    }
}

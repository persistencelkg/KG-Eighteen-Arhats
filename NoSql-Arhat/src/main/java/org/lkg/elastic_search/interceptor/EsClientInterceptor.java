package org.lkg.elastic_search.interceptor;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.aop.framework.ProxyFactoryBean;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/21 3:37 PM
 */
//@Aspect
//@Component
@AllArgsConstructor
public class EsClientInterceptor implements MethodInterceptor {


    private final List<EsInterceptor> esInterceptorList;
    private final static String METHOD = "performRequest";


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!Objects.equals(invocation.getMethod().getName(), METHOD)) {
            return invocation.proceed();
        }
        Object[] arguments = invocation.getArguments();
        if (Objects.isNull(arguments) || arguments.length < 1) {
            return invocation.proceed();
        }
        Object arg = arguments[0];
        String sql = "";
        if (arg instanceof SearchRequest) {
            System.out.println(esQueryToSQL((SearchRequest) arg));
        }
        return new DefaultEsInterceptor(sql, () -> {
            try {
                return invocation.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }).process();
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


    public static Object proxy(RestClient client, List<EsInterceptor> list) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(client);
        proxyFactoryBean.setProxyTargetClass(true);
        proxyFactoryBean.addAdvice(new EsClientInterceptor(list));
        return proxyFactoryBean.getObject();
    }

    class DefaultEsInterceptor implements EsInterceptor.EsChain {

        private final Iterator<EsInterceptor> iterator;
        private final String source;
        private final Supplier<?> supplier;

        public DefaultEsInterceptor(String source, Supplier<?> supplier) {
            this.source = source;
            this.iterator = esInterceptorList.iterator();
            this.supplier = supplier;
        }


        @Override
        public Object process() throws Throwable {
            return iterator.hasNext() ? iterator.next().intercept(this) : this.supplier.get();
        }

        @Override
        public String esDsl() {
            return source;
        }
    }
}

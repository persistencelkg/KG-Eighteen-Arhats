package org.lkg.metric.sql.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.lkg.core.DynamicConfigManger;
import org.lkg.enums.TrueFalseEnum;
import org.lkg.metric.sql.CustomMybatisInterceptor;
import org.lkg.metric.sql.FuzzySqlUtil;
import org.lkg.metric.sql.SqlEventTracker;

import javax.validation.groups.Default;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/28 8:51 PM
 */
//MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler


/*
 * Executor 的粒度比StatementHandler 粒度要打，适合对这个 SQL 执行流程、事务管理、缓存处理或批处理的场景。它关注的是整体的执行策略和管理。
 *<code>
         @Signature(type = Executor.class, method = "commit", args = {boolean.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
# 对应示例： SimpleExecutor target = (SimpleExecutor) invocation.getTarget();
 </code>
 *  的粒度比StatementHandler 关注到每个sql的执行过程，包括prepare、parameterize、

 * */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
})
@Slf4j
public class MybatisStatementInterceptor implements Interceptor {

    private final List<CustomMybatisInterceptor> list;

    public MybatisStatementInterceptor(List<CustomMybatisInterceptor> list) {
        this.list = list;
    }


    @Override
    public Object intercept(Invocation invocation) throws Exception {
        String sql = "";
        boolean suc = true;
        long start = System.nanoTime();
        try {
            StatementHandler target = (StatementHandler) invocation.getTarget();
            Method method = invocation.getMethod();
            String name = method.getName();
            BoundSql boundSql = target.getBoundSql();
            sql = boundSql.getSql();
            // mybatis plus 会为sql增加换行符，给他去掉
            sql = sql.replaceAll("\n", "");
            ParameterHandler args = target.getParameterHandler();
            Object obj = args.getParameterObject();

            if (obj instanceof MapperMethod.ParamMap) {
                StringBuilder sb = new StringBuilder();
                ((MapperMethod.ParamMap) obj).forEach((k, v) -> {
                    sb.append(k).append("=");
                    // support mp param print
                    if (v instanceof LambdaQueryWrapper) {
                        sb.append(((LambdaQueryWrapper<?>) v).getParamNameValuePairs().values()).append(";");
                    } else {
                        sb.append(v).append(";");
                    }
                });
                obj = sb.toString();
            }
            sql = name.contains("batch") ? name + " " + sql : sql;
            final String finalSql = sql;
            final Object finalObj = obj;
            DynamicConfigManger.initAndRegistChangeEvent("monit.sql.print.enable", DynamicConfigManger::getInt, ref -> {
                if (TrueFalseEnum.isTrue(ref)) {
                    log.info("monit full sql:{}, args:{}", finalSql, finalObj);
                }
            });
            try {
                sql = FuzzySqlUtil.cleanStatement(sql);
            } catch (JSQLParserException e2) {
                log.error("sql:{} parse error", sql);
            }
            return new DefaultChain(() -> {
                try {
                    return invocation.proceed();
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }, sql).process();
        } catch (Exception e) {
            suc = false;
            throw e;
        } finally {
            SqlEventTracker.monitorSql(sql, suc, start);

        }
    }

    private class DefaultChain implements CustomMybatisInterceptor.Chain {

        private final Iterator<CustomMybatisInterceptor> iterator;
        private final Supplier<Object> supplier;

        private final String sql;

        public DefaultChain(Supplier<Object> supplier, String sql) {
            this.iterator = list.iterator();
            this.supplier = supplier;
            this.sql = sql;
        }


        @Override
        public String sql() {
            return sql;
        }

        @Override
        public Object process() throws Exception {
            if (iterator.hasNext()) {
                return iterator.next().interceptor(this);
            }
            return this.supplier.get();
        }
    }
}

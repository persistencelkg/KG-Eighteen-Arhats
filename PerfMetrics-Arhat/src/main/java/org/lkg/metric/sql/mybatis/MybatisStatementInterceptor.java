package org.lkg.metric.sql.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.lkg.metric.sql.SqlEventTracker;

import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.Properties;

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
        @Signature(type = StatementHandler.class, method = "parameterize", args = {Statement.class}),
})
public class MybatisStatementInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Exception {
        String sql = "";
        boolean suc = true;
        long start = System.nanoTime();
        try {
            StatementHandler target = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = target.getBoundSql();
            sql = boundSql.getSql();
            Object[] args = invocation.getArgs();
            System.out.println(sql);
            return invocation.proceed();
        } catch (InvocationTargetException | IllegalAccessException e) {
            suc = false;
            throw new Exception(e);
        } finally {
            SqlEventTracker.monitorSql(sql, suc, start);
        }
    }
}

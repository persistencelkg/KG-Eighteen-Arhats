package org.lkg.metric.sql.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
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
import org.lkg.metric.sql.SqlEventTracker;

import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;

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


    @Override
    public Object intercept(Invocation invocation) throws Exception {
        String sql = "";
        boolean suc = true;
        long start = System.nanoTime();
        try {
            StatementHandler target = (StatementHandler) invocation.getTarget();
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
            final String finalSql = sql;
            final Object finalObj = obj;
            DynamicConfigManger.initAndRegistChangeEvent("monit.sql.print.enable", DynamicConfigManger::getInt, ref -> {
                if (TrueFalseEnum.isTrue(ref)) {
                    log.info("monit full sql:{}, args:{}", finalSql, finalObj);
                }
            });
            return invocation.proceed();
        } catch (InvocationTargetException | IllegalAccessException e) {
            suc = false;
            throw new Exception(e);
        } finally {
            SqlEventTracker.monitorSql(sql, suc, start);
        }
    }
}

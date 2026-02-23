package com.eagleeye.common.plugin;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * MyBatis审计日志拦截器
 * 自动记录核心数据表的变更日志
 * 拦截insert、update、delete操作
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class AuditLogInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];

        // 只拦截insert、update、delete操作
        if (sqlCommandType == SqlCommandType.INSERT ||
            sqlCommandType == SqlCommandType.UPDATE ||
            sqlCommandType == SqlCommandType.DELETE) {

            String tableId = mappedStatement.getId();
            log.info("审计日志：操作类型={}, 表ID={}, 参数={}", sqlCommandType, tableId, parameter);

            // TODO: 根据业务需要，将变更记录写入审计日志表
            // 可以记录操作人、操作时间、操作类型、变更前后数据等
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 配置属性
    }
}

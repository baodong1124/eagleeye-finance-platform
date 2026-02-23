package com.eagleeye.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus字段自动填充处理器
 * 自动填充创建时间、更新时间、创建人、更新人等字段
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        // 填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

        // 填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // TODO: 填充创建人（需要从上下文获取当前用户ID）
        // this.strictInsertFill(metaObject, "createBy", Long.class, getCurrentUserId());
        // this.strictInsertFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // 填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // TODO: 填充更新人
        // this.strictUpdateFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

    /**
     * 获取当前用户ID
     * TODO: 需要从安全上下文或ThreadLocal中获取
     */
    private Long getCurrentUserId() {
        return 1L; // 默认返回1，实际项目需要替换为真实的用户ID获取逻辑
    }
}

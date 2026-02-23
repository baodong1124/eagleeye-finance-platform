package com.eagleeye.common.base;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础查询DTO
 * 包含分页参数
 */
@Data
public abstract class BaseQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式：asc/desc
     */
    private String order;
}

package com.eagleeye.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体类
 * 对应数据库表：sys_permission
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @TableId(value = "permission_id", type = IdType.AUTO)
    private Long permissionId;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码（如：user:add, user:delete）
     */
    private String permissionCode;

    /**
     * 权限类型：1-菜单，2-按钮，3-接口
     */
    private Integer type;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 路径
     */
    private String path;

    /**
     * 权限状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序序号
     */
    private Integer sort;

    /**
     * 图标
     */
    private String icon;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}

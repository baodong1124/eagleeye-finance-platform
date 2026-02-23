package com.eagleeye.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 部门实体类
 * 对应数据库表：sys_department
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_department")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门层级
     */
    private Integer level;

    /**
     * 部门负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 部门地址
     */
    private String address;

    /**
     * 部门状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序序号
     */
    private Integer sort;

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

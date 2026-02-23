package com.eagleeye.account.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预算实体类
 * 对应数据库表：fin_budget
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("fin_budget")
public class Budget implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预算ID
     */
    @TableId(value = "budget_id", type = IdType.AUTO)
    private Long budgetId;

    /**
     * 预算编号
     */
    private String budgetNo;

    /**
     * 预算名称
     */
    private String budgetName;

    /**
     * 预算类型：1-年度预算，2-季度预算，3-月度预算
     */
    private Integer budgetType;

    /**
     * 预算年度
     */
    private Integer budgetYear;

    /**
     * 预算月份
     */
    private Integer budgetMonth;

    /**
     * 归属类型：1-部门，2-项目
     */
    private Integer belongType;

    /**
     * 归属ID（部门ID或项目ID）
     */
    private Long belongId;

    /**
     * 预算总额
     */
    private BigDecimal totalAmount;

    /**
     * 已使用金额
     */
    private BigDecimal usedAmount;

    /**
     * 剩余金额
     */
    private BigDecimal remainingAmount;

    /**
     * 预算状态：0-草稿，1-生效，2-过期
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

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

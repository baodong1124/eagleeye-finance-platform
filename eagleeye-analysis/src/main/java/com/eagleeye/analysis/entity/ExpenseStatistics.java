package com.eagleeye.analysis.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 费用统计实体类
 * 对应数据库表：ana_expense_statistics
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ana_expense_statistics")
public class ExpenseStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    @TableId(value = "stat_id", type = IdType.AUTO)
    private Long statId;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 统计类型：1-按天，2-按周，3-按月，4-按季度，5-按年
     */
    private Integer statType;

    /**
     * 归属类型：1-部门，2-项目，3-个人
     */
    private Integer belongType;

    /**
     * 归属ID
     */
    private Long belongId;

    /**
     * 归属名称
     */
    private String belongName;

    /**
     * 报销总金额
     */
    private BigDecimal totalAmount;

    /**
     * 报销单数量
     */
    private Integer orderCount;

    /**
     * 已支付金额
     */
    private BigDecimal paidAmount;

    /**
     * 已支付数量
     */
    private Integer paidCount;

    /**
     * 平均报销金额
     */
    private BigDecimal avgAmount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

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

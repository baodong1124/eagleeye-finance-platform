package com.eagleeye.expense.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报销明细实体类
 * 对应数据库表：exp_expense_item
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("exp_expense_item")
public class ExpenseItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    /**
     * 报销单ID
     */
    private Long orderId;

    /**
     * 明细序号
     */
    private Integer itemNo;

    /**
     * 费用名称
     */
    private String itemName;

    /**
     * 费用金额
     */
    private BigDecimal amount;

    /**
     * 费用日期
     */
    private String expenseDate;

    /**
     * 发票类型：1-增值税专用发票，2-增值税普通发票，3-其他
     */
    private Integer invoiceType;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 发票图片URL
     */
    private String invoiceImage;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}

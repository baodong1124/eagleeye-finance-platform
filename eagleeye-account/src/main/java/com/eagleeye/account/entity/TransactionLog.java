package com.eagleeye.account.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户流水记录实体类
 * 对应数据库表：fin_transaction_log
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("fin_transaction_log")
public class TransactionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流水ID
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 流水号（唯一）
     */
    private String transactionNo;

    /**
     * 关联订单号
     */
    private String orderNo;

    /**
     * 账户ID
     */
    private Long accountId;

    /**
     * 交易类型：1-收入，2-支出，3-冻结，4-解冻
     */
    private Integer transactionType;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 交易前余额
     */
    private BigDecimal beforeBalance;

    /**
     * 交易后余额
     */
    private BigDecimal afterBalance;

    /**
     * 业务类型：1-报销，2-转账，3-充值，4-提现，5-预算冻结
     */
    private Integer businessType;

    /**
     * 业务描述
     */
    private String businessDesc;

    /**
     * 交易状态：0-处理中，1-成功，2-失败
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

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

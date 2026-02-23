package com.eagleeye.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 * 对应数据库表：pay_payment_record
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pay_payment_record")
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付记录ID
     */
    @TableId(value = "payment_id", type = IdType.AUTO)
    private Long paymentId;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 关联订单号
     */
    private String orderNo;

    /**
     * 支付类型：1-报销支付，2-转账，3-提现
     */
    private Integer paymentType;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 收款人ID
     */
    private Long payeeId;

    /**
     * 收款人姓名
     */
    private String payeeName;

    /**
     * 收款人账号
     */
    private String payeeAccount;

    /**
     * 收款银行
     */
    private String payeeBank;

    /**
     * 支付状态：0-待支付，1-支付中，2-支付成功，3-支付失败
     */
    private Integer status;

    /**
     * 支付渠道：1-银行转账，2-支付宝，3-微信，4-内部账户
     */
    private Integer channel;

    /**
     * 第三方支付流水号
     */
    private String transactionId;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 失败原因
     */
    private String failReason;

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

package com.eagleeye.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 对账记录实体类
 * 对应数据库表：pay_reconciliation_record
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pay_reconciliation_record")
public class ReconciliationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对账记录ID
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * 对账日期
     */
    private LocalDate reconcileDate;

    /**
     * 对账类型：1-自动对账，2-手动对账
     */
    private Integer reconcileType;

    /**
     * 对账状态：0-进行中，1-成功，2-失败
     */
    private Integer status;

    /**
     * 总笔数
     */
    private Integer totalCount;

    /**
     * 成功笔数
     */
    private Integer successCount;

    /**
     * 失败笔数
     */
    private Integer failCount;

    /**
     * 总金额
     */
    private java.math.BigDecimal totalAmount;

    /**
     * 差异金额
     */
    private java.math.BigDecimal diffAmount;

    /**
     * 差异说明
     */
    private String diffDesc;

    /**
     * 对账人ID
     */
    private Long operatorId;

    /**
     * 对账人姓名
     */
    private String operatorName;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

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

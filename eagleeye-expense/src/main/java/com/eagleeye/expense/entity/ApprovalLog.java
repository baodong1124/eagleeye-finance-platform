package com.eagleeye.expense.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审批流水实体类
 * 对应数据库表：exp_approval_log
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("exp_approval_log")
public class ApprovalLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 审批流水ID
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 报销单ID
     */
    private Long orderId;

    /**
     * 报销单号
     */
    private String orderNo;

    /**
     * 审批节点
     */
    private String node;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批结果：1-通过，2-拒绝，3-撤回
     */
    private Integer approvalResult;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

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

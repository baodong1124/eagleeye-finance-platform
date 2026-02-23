package com.eagleeye.expense.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报销单实体类
 * 对应数据库表：exp_expense_order
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("exp_expense_order")
public class ExpenseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报销单ID
     */
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 报销单号（唯一）
     */
    private String orderNo;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 报销总金额
     */
    private BigDecimal totalAmount;

    /**
     * 报销类型：1-差旅费，2-办公费，3-招待费，4-其他
     */
    private Integer expenseType;

    /**
     * 报销说明
     */
    private String description;

    /**
     * 报销日期
     */
    private LocalDateTime expenseDate;

    /**
     * 审批状态：0-待提交，1-待审批，2-审批中，3-已通过，4-已拒绝，5-已撤回
     */
    private Integer approvalStatus;

    /**
     * 当前审批节点
     */
    private String currentNode;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批意见
     */
    private String approvalComment;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 支付状态：0-未支付，1-支付中，2-已支付
     */
    private Integer paymentStatus;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 附件URL（逗号分隔）
     */
    private String attachmentUrls;

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

package com.eagleeye.account.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资金账户实体类
 * 对应数据库表：fin_account
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("fin_account")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账户ID
     */
    @TableId(value = "account_id", type = IdType.AUTO)
    private Long accountId;

    /**
     * 账户号（唯一）
     */
    private String accountNo;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 账户类型：1-资金账户，2-预算账户
     */
    private Integer accountType;

    /**
     * 归属类型：1-部门，2-项目
     */
    private Integer belongType;

    /**
     * 归属ID（部门ID或项目ID）
     */
    private Long belongId;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;

    /**
     * 账户状态：0-冻结，1-正常
     */
    private Integer status;

    /**
     * 信用额度
     */
    private BigDecimal creditLimit;

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
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}

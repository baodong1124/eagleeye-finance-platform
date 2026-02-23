package com.eagleeye.expense.service;

import com.eagleeye.expense.dto.ExpenseSubmitDTO;

/**
 * 报销单提交Service接口
 * 核心业务场景：提交报销单并同步扣减预算
 */
public interface ExpenseSubmitService {

    /**
     * 提交报销单
     * 核心业务流程：
     * 1. 验证预算余额
     * 2. 冻结预算金额
     * 3. 生成订单号
     * 4. 创建报销单
     * 5. 记录流水
     * 6. 发送审批通知消息
     * 7. 记录审计日志
     *
     * @param applicantId 申请人ID
     * @param dto         报销单提交DTO
     * @return 报销单ID
     */
    Long submitExpense(Long applicantId, ExpenseSubmitDTO dto);

    /**
     * 审批报销单
     *
     * @param orderId   报销单ID
     * @param approverId 审批人ID
     * @param approved  是否通过
     * @param comment   审批意见
     * @return 是否成功
     */
    boolean approveExpense(Long orderId, Long approverId, Boolean approved, String comment);
}

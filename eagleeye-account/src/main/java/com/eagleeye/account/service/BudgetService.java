package com.eagleeye.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eagleeye.account.entity.Budget;

import java.math.BigDecimal;

/**
 * 预算Service接口
 */
public interface BudgetService extends IService<Budget> {

    /**
     * 根据归属查询生效的预算
     *
     * @param belongType 归属类型
     * @param belongId   归属ID
     * @return 预算信息
     */
    Budget getActiveBudget(Integer belongType, Long belongId);

    /**
     * 冻结预算金额
     *
     * @param budgetId 预算ID
     * @param amount   金额
     * @return 是否成功
     */
    boolean freezeBudget(Long budgetId, BigDecimal amount);

    /**
     * 解冻预算金额
     *
     * @param budgetId 预算ID
     * @param amount   金额
     * @return 是否成功
     */
    boolean unfreezeBudget(Long budgetId, BigDecimal amount);

    /**
     * 使用预算（消费冻结金额）
     *
     * @param budgetId 预算ID
     * @param amount   金额
     * @return 是否成功
     */
    boolean useBudget(Long budgetId, BigDecimal amount);

    /**
     * 检查预算余额是否充足
     *
     * @param budgetId 预算ID
     * @param amount   金额
     * @return 是否充足
     */
    boolean checkBudgetBalance(Long budgetId, BigDecimal amount);
}

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

    /**
     * 方案1：使用数据库悲观锁扣减预算
     * 适用于强一致性要求场景，读写分离或分布式场景
     *
     * @param budgetId 预算ID
     * @param amount   扣减金额
     * @return 是否成功
     */
    boolean deductBudgetWithDbLock(Long budgetId, BigDecimal amount);

    /**
     * 方案2：使用内存原子操作扣减预算
     * 将预算余额加载到内存，使用CAS原子操作实现并发控制
     * 避免频繁数据库IO和锁竞争，性能更高
     *
     * @param budgetId 预算ID
     * @param amount   扣减金额
     * @return 是否成功
     */
    boolean deductBudgetWithMemoryAtomic(Long budgetId, BigDecimal amount);
}

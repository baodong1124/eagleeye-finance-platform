package com.eagleeye.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eagleeye.account.entity.Budget;
import com.eagleeye.account.mapper.BudgetMapper;
import com.eagleeye.account.service.BudgetService;
import com.eagleeye.common.lock.BudgetMemoryCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 预算Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl extends ServiceImpl<BudgetMapper, Budget> implements BudgetService {

    private final BudgetMapper budgetMapper;
    private final BudgetMemoryCache budgetMemoryCache;

    @Override
    public Budget getActiveBudget(Integer belongType, Long belongId) {
        LambdaQueryWrapper<Budget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Budget::getBelongType, belongType)
                .eq(Budget::getBelongId, belongId)
                .eq(Budget::getStatus, 1)
                .orderByDesc(Budget::getCreateTime)
                .last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeBudget(Long budgetId, BigDecimal amount) {
        Budget budget = getById(budgetId);
        if (budget == null) {
            log.error("预算不存在，budgetId={}", budgetId);
            return false;
        }

        // 检查剩余金额是否充足
        if (budget.getRemainingAmount().compareTo(amount) < 0) {
            log.error("预算余额不足，budgetId={}, remainingAmount={}, amount={}", budgetId, budget.getRemainingAmount(), amount);
            return false;
        }

        // 冻结预算（冻结金额从可用余额中扣除）
        budget.setRemainingAmount(budget.getRemainingAmount().subtract(amount));

        boolean success = updateById(budget);
        if (success) {
            log.info("冻结预算成功，budgetId={}, amount={}", budgetId, amount);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfreezeBudget(Long budgetId, BigDecimal amount) {
        Budget budget = getById(budgetId);
        if (budget == null) {
            log.error("预算不存在，budgetId={}", budgetId);
            return false;
        }

        // 解冻预算（恢复到可用余额）
        budget.setRemainingAmount(budget.getRemainingAmount().add(amount));

        boolean success = updateById(budget);
        if (success) {
            log.info("解冻预算成功，budgetId={}, amount={}", budgetId, amount);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useBudget(Long budgetId, BigDecimal amount) {
        Budget budget = getById(budgetId);
        if (budget == null) {
            log.error("预算不存在，budgetId={}", budgetId);
            return false;
        }

        // 使用预算（增加已使用金额）
        budget.setUsedAmount(budget.getUsedAmount().add(amount));

        boolean success = updateById(budget);
        if (success) {
            log.info("使用预算成功，budgetId={}, amount={}", budgetId, amount);
        }
        return success;
    }

    @Override
    public boolean checkBudgetBalance(Long budgetId, BigDecimal amount) {
        Budget budget = getById(budgetId);
        if (budget == null) {
            return false;
        }
        return budget.getRemainingAmount().compareTo(amount) >= 0;
    }

    /**
     * 方案1：使用数据库悲观锁扣减预算
     * 适用于强一致性要求场景，读写分离或分布式场景
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductBudgetWithDbLock(Long budgetId, BigDecimal amount) {
        // 使用悲观锁查询（锁定这一行）
        Budget budget = budgetMapper.selectForUpdate(budgetId);
        if (budget == null) {
            log.error("预算不存在，budgetId={}", budgetId);
            return false;
        }

        // 检查余额是否充足
        if (budget.getRemainingAmount().compareTo(amount) < 0) {
            log.warn("预算余额不足，budgetId={}, remainingAmount={}, requestAmount={}",
                    budgetId, budget.getRemainingAmount(), amount);
            return false;
        }

        // 扣减预算
        budget.setUsedAmount(budget.getUsedAmount().add(amount));
        budget.setRemainingAmount(budget.getRemainingAmount().subtract(amount));

        boolean success = updateById(budget);
        if (success) {
            log.info("悲观锁扣减预算成功，budgetId={}, amount={}, remainingAmount={}",
                    budgetId, amount, budget.getRemainingAmount());
        } else {
            log.error("更新预算失败，budgetId={}", budgetId);
        }
        return success;
    }

    /**
     * 方案2：使用内存原子操作扣减预算
     * 将预算余额加载到内存，使用CAS原子操作实现并发控制
     * 避免频繁数据库IO和锁竞争，性能更高
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductBudgetWithMemoryAtomic(Long budgetId, BigDecimal amount) {
        // 1. 查询预算信息
        Budget budget = getById(budgetId);
        if (budget == null) {
            log.error("预算不存在，budgetId={}", budgetId);
            return false;
        }

        // 2. 首次加载预算余额到内存
        if (budgetMemoryCache.getRemaining(budgetId) == null) {
            budgetMemoryCache.loadBudget(budgetId, budget.getRemainingAmount());
        }

        // 3. 使用CAS原子操作在内存中扣减
        boolean memoryDeductSuccess = budgetMemoryCache.deductWithAtomic(budgetId, amount);
        if (!memoryDeductSuccess) {
            return false;
        }

        // 4. 同步更新到数据库（异步场景可以改为定时批量更新）
        Budget updateBudget = getById(budgetId);
        BigDecimal newRemaining = budgetMemoryCache.getRemaining(budgetId);
        updateBudget.setUsedAmount(updateBudget.getUsedAmount().add(amount));
        updateBudget.setRemainingAmount(newRemaining);

        boolean dbUpdateSuccess = updateById(updateBudget);
        if (!dbUpdateSuccess) {
            log.error("同步数据库失败，budgetId={}", budgetId);
            // 回滚内存缓存
            budgetMemoryCache.reloadBudget(budgetId, budget.getRemainingAmount());
            return false;
        }

        log.info("内存原子扣减预算成功，budgetId={}, amount={}, remainingAmount={}",
                budgetId, amount, newRemaining);
        return true;
    }
}

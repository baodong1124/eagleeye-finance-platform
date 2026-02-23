package com.eagleeye.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eagleeye.account.entity.Budget;
import com.eagleeye.account.mapper.BudgetMapper;
import com.eagleeye.account.service.BudgetService;
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
}

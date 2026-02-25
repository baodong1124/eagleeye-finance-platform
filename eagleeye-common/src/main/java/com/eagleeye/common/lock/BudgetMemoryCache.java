package com.eagleeye.common.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 预算内存缓存
 * 方案2：将预算余额加载到内存，使用原子操作实现并发控制
 *
 * 使用 ConcurrentHashMap + AtomicReference 实现细粒度的并发控制
 * 避免频繁数据库IO和锁竞争，极大提升性能
 */
@Slf4j
@Component
public class BudgetMemoryCache {

    /**
     * 预算余额缓存：budgetId -> 剩余金额
     * 使用 AtomicReference 保证原子性
     */
    private final ConcurrentHashMap<Long, AtomicReference<BigDecimal>> budgetCache = new ConcurrentHashMap<>();

    /**
     * 初始化/加载预算余额到内存
     *
     * @param budgetId        预算ID
     * @param remainingAmount 剩余金额
     */
    public void loadBudget(Long budgetId, BigDecimal remainingAmount) {
        budgetCache.put(budgetId, new AtomicReference<>(remainingAmount));
        log.info("预算余额已加载到内存，budgetId={}, remainingAmount={}", budgetId, remainingAmount);
    }

    /**
     * 使用CAS（Compare-And-Swap）原子操作扣减预算余额
     *
     * @param budgetId 预算ID
     * @param amount   扣减金额
     * @return 是否成功
     */
    public boolean deductWithAtomic(Long budgetId, BigDecimal amount) {
        AtomicReference<BigDecimal> remainingRef = budgetCache.get(budgetId);
        if (remainingRef == null) {
            log.error("预算未加载到内存，budgetId={}", budgetId);
            return false;
        }

        // 使用 CAS 原子操作实现无锁并发扣减
        while (true) {
            BigDecimal currentRemaining = remainingRef.get();

            // 检查余额是否充足
            if (currentRemaining.compareTo(amount) < 0) {
                log.warn("预算余额不足，budgetId={}, remainingAmount={}, requestAmount={}",
                        budgetId, currentRemaining, amount);
                return false;
            }

            // 计算新的余额
            BigDecimal newRemaining = currentRemaining.subtract(amount);

            // CAS 操作：如果当前值未被其他线程修改，则更新
            if (remainingRef.compareAndSet(currentRemaining, newRemaining)) {
                log.info("原子扣减预算成功，budgetId={}, amount={}, remainingAmount={}",
                        budgetId, amount, newRemaining);
                return true;
            }

            // CAS 失败，说明其他线程修改了余额，重试
            log.debug("CAS失败，重试中... budgetId={}", budgetId);
        }
    }

    /**
     * 获取当前预算余额（用于调试）
     *
     * @param budgetId 预算ID
     * @return 剩余金额
     */
    public BigDecimal getRemaining(Long budgetId) {
        AtomicReference<BigDecimal> ref = budgetCache.get(budgetId);
        return ref != null ? ref.get() : null;
    }

    /**
     * 重新加载预算余额（从数据库同步到内存）
     *
     * @param budgetId        预算ID
     * @param remainingAmount 剩余金额
     */
    public void reloadBudget(Long budgetId, BigDecimal remainingAmount) {
        loadBudget(budgetId, remainingAmount);
        log.info("预算余额已重新加载，budgetId={}, remainingAmount={}", budgetId, remainingAmount);
    }

    /**
     * 清除缓存
     *
     * @param budgetId 预算ID
     */
    public void clearCache(Long budgetId) {
        budgetCache.remove(budgetId);
        log.info("预算缓存已清除，budgetId={}", budgetId);
    }
}

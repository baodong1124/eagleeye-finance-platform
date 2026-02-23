package com.eagleeye.account.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eagleeye.account.entity.Account;
import com.eagleeye.account.mapper.AccountMapper;
import com.eagleeye.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 账户Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    private final AccountMapper accountMapper;

    @Override
    public Account getAccountByNo(String accountNo) {
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getAccountNo, accountNo);
        return getOne(wrapper);
    }

    @Override
    public Account getAccountByBelong(Integer belongType, Long belongId) {
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getBelongType, belongType)
                .eq(Account::getBelongId, belongId)
                .eq(Account::getStatus, 1);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeAmount(Long accountId, BigDecimal amount) {
        Account account = getById(accountId);
        if (account == null) {
            log.error("账户不存在，accountId={}", accountId);
            return false;
        }

        // 检查余额是否充足
        if (account.getBalance().compareTo(amount) < 0) {
            log.error("账户余额不足，accountId={}, balance={}, amount={}", accountId, account.getBalance(), amount);
            return false;
        }

        // 冻结金额
        account.setBalance(account.getBalance().subtract(amount));
        account.setFrozenAmount(account.getFrozenAmount().add(amount));

        boolean success = updateById(account);
        if (success) {
            log.info("冻结账户金额成功，accountId={}, amount={}", accountId, amount);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfreezeAmount(Long accountId, BigDecimal amount) {
        Account account = getById(accountId);
        if (account == null) {
            log.error("账户不存在，accountId={}", accountId);
            return false;
        }

        // 检查冻结金额是否充足
        if (account.getFrozenAmount().compareTo(amount) < 0) {
            log.error("账户冻结金额不足，accountId={}, frozenAmount={}, amount={}", accountId, account.getFrozenAmount(), amount);
            return false;
        }

        // 解冻金额
        account.setFrozenAmount(account.getFrozenAmount().subtract(amount));
        account.setBalance(account.getBalance().add(amount));

        boolean success = updateById(account);
        if (success) {
            log.info("解冻账户金额成功，accountId={}, amount={}", accountId, amount);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductBalance(Long accountId, BigDecimal amount) {
        // 使用乐观锁扣减余额
        Account account = getById(accountId);
        if (account == null) {
            log.error("账户不存在，accountId={}", accountId);
            return false;
        }

        // 检查余额是否充足
        if (account.getBalance().compareTo(amount) < 0) {
            log.error("账户余额不足，accountId={}, balance={}, amount={}", accountId, account.getBalance(), amount);
            return false;
        }

        // 扣减余额（MyBatis-Plus会自动处理乐观锁）
        account.setBalance(account.getBalance().subtract(amount));

        boolean success = updateById(account);
        if (!success) {
            log.error("扣减余额失败（乐观锁冲突），accountId={}", accountId);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addBalance(Long accountId, BigDecimal amount) {
        Account account = getById(accountId);
        if (account == null) {
            log.error("账户不存在，accountId={}", accountId);
            return false;
        }

        // 增加余额
        account.setBalance(account.getBalance().add(amount));

        boolean success = updateById(account);
        if (success) {
            log.info("增加账户余额成功，accountId={}, amount={}", accountId, amount);
        }
        return success;
    }

    @Override
    public boolean checkBalance(Long accountId, BigDecimal amount) {
        Account account = getById(accountId);
        if (account == null) {
            return false;
        }
        return account.getBalance().compareTo(amount) >= 0;
    }
}

package com.eagleeye.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eagleeye.account.entity.Account;

import java.math.BigDecimal;

/**
 * 账户Service接口
 */
public interface AccountService extends IService<Account> {

    /**
     * 根据账户号查询账户
     *
     * @param accountNo 账户号
     * @return 账户信息
     */
    Account getAccountByNo(String accountNo);

    /**
     * 根据归属类型和归属ID查询账户
     *
     * @param belongType 归属类型
     * @param belongId   归属ID
     * @return 账户信息
     */
    Account getAccountByBelong(Integer belongType, Long belongId);

    /**
     * 冻结账户金额
     *
     * @param accountId 账户ID
     * @param amount    金额
     * @return 是否成功
     */
    boolean freezeAmount(Long accountId, BigDecimal amount);

    /**
     * 解冻账户金额
     *
     * @param accountId 账户ID
     * @param amount    金额
     * @return 是否成功
     */
    boolean unfreezeAmount(Long accountId, BigDecimal amount);

    /**
     * 扣减账户余额（使用乐观锁）
     *
     * @param accountId 账户ID
     * @param amount    金额
     * @return 是否成功
     */
    boolean deductBalance(Long accountId, BigDecimal amount);

    /**
     * 增加账户余额
     *
     * @param accountId 账户ID
     * @param amount    金额
     * @return 是否成功
     */
    boolean addBalance(Long accountId, BigDecimal amount);

    /**
     * 检查账户余额是否充足
     *
     * @param accountId 账户ID
     * @param amount    金额
     * @return 是否充足
     */
    boolean checkBalance(Long accountId, BigDecimal amount);
}

package com.eagleeye.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eagleeye.account.entity.TransactionLog;

/**
 * 流水记录Service接口
 */
public interface TransactionLogService extends IService<TransactionLog> {

    /**
     * 创建流水记录
     *
     * @param transactionLog 流水记录
     * @return 流水记录ID
     */
    Long createTransactionLog(TransactionLog transactionLog);

    /**
     * 根据订单号查询流水记录
     *
     * @param orderNo 订单号
     * @return 流水记录
     */
    TransactionLog getTransactionByOrderNo(String orderNo);
}

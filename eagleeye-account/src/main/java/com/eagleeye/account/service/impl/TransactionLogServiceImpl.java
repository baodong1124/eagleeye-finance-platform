package com.eagleeye.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eagleeye.account.entity.TransactionLog;
import com.eagleeye.account.mapper.TransactionLogMapper;
import com.eagleeye.account.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 流水记录Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionLogServiceImpl extends ServiceImpl<TransactionLogMapper, TransactionLog> implements TransactionLogService {

    private final TransactionLogMapper transactionLogMapper;

    @Override
    public Long createTransactionLog(TransactionLog transactionLog) {
        transactionLog.setCreateTime(LocalDateTime.now());
        boolean success = save(transactionLog);
        if (success) {
            log.info("创建流水记录成功，logId={}", transactionLog.getLogId());
            return transactionLog.getLogId();
        } else {
            log.error("创建流水记录失败");
            return null;
        }
    }

    @Override
    public TransactionLog getTransactionByOrderNo(String orderNo) {
        return lambdaQuery()
                .eq(TransactionLog::getOrderNo, orderNo)
                .one();
    }
}

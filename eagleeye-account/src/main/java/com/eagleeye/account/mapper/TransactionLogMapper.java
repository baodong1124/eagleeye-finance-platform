package com.eagleeye.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eagleeye.account.entity.TransactionLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流水记录Mapper接口
 */
@Mapper
public interface TransactionLogMapper extends BaseMapper<TransactionLog> {
}

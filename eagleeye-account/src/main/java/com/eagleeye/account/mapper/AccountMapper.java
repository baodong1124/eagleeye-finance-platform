package com.eagleeye.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eagleeye.account.entity.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账户Mapper接口
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}

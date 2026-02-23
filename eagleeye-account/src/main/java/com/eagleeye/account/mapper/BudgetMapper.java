package com.eagleeye.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eagleeye.account.entity.Budget;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预算Mapper接口
 */
@Mapper
public interface BudgetMapper extends BaseMapper<Budget> {
}

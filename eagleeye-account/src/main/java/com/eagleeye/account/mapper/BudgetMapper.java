package com.eagleeye.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eagleeye.account.entity.Budget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 预算Mapper接口
 */
@Mapper
public interface BudgetMapper extends BaseMapper<Budget> {

    /**
     * 方案1：使用悲观锁查询预算（FOR UPDATE）
     *
     * @param budgetId 预算ID
     * @return 预算信息
     */
    @Select("SELECT * FROM fin_budget WHERE budget_id = #{budgetId} AND deleted = 0 FOR UPDATE")
    Budget selectForUpdate(@Param("budgetId") Long budgetId);
}

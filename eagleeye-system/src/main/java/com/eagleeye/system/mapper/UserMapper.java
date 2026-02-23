package com.eagleeye.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eagleeye.system.dto.UserQueryDTO;
import com.eagleeye.system.entity.User;
import com.eagleeye.system.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户列表
     *
     * @param page     分页对象
     * @param queryDTO 查询条件
     * @return 用户列表
     */
    IPage<UserVO> selectUserPage(Page<UserVO> page, @Param("query") UserQueryDTO queryDTO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);
}

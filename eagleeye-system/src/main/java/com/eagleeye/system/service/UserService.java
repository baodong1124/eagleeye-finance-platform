package com.eagleeye.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eagleeye.common.result.Result;
import com.eagleeye.system.dto.UserQueryDTO;
import com.eagleeye.system.entity.User;
import com.eagleeye.system.vo.UserVO;

/**
 * 用户Service接口
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表
     *
     * @param current  当前页
     * @param size     每页条数
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Result<IPage<UserVO>> pageUser(Long current, Long size, UserQueryDTO queryDTO);

    /**
     * 根据ID查询用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    Result<UserVO> getUserById(Long userId);

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 操作结果
     */
    Result<Void> createUser(User user);

    /**
     * 更新用户
     *
     * @param user 用户信息
     * @return 操作结果
     */
    Result<Void> updateUser(User user);

    /**
     * 删除用户（逻辑删除）
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> deleteUser(Long userId);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
}

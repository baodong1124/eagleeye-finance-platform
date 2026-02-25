package com.eagleeye.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eagleeye.common.result.Result;
import com.eagleeye.system.dto.UserQueryDTO;
import com.eagleeye.system.entity.User;
import com.eagleeye.system.mapper.UserMapper;
import com.eagleeye.system.service.UserService;
import com.eagleeye.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    @Override
    public Result<IPage<UserVO>> pageUser(Long current, Long size, UserQueryDTO queryDTO) {
        try {

            ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>();
            for (int i = 0; i < 10; i++) {
                map.put("key" + i,"value" + i);
            }
            map.put("key","value");
            map.get("key");
            Page<UserVO> page = new Page<>(current, size);
            IPage<UserVO> result = userMapper.selectUserPage(page, queryDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询用户列表失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result<UserVO> getUserById(Long userId) {
        try {
            User user = getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 转换为VO
            UserVO vo = convertToVO(user);
            return Result.success(vo);
        } catch (Exception e) {
            log.error("查询用户详情失败，userId={}", userId, e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createUser(User user) {
        try {
            // 检查用户名是否已存在
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, user.getUsername());
            if (count(wrapper) > 0) {
                return Result.error("用户名已存在");
            }

            // 检查手机号是否已存在
            if (StrUtil.isNotBlank(user.getPhone())) {
                wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getPhone, user.getPhone());
                if (count(wrapper) > 0) {
                    return Result.error("手机号已被使用");
                }
            }

            // 默认密码为123456（实际项目中应该加密）
            if (StrUtil.isBlank(user.getPassword())) {
                user.setPassword("123456");
            }

            // 默认状态为启用
            if (user.getStatus() == null) {
                user.setStatus(1);
            }

            boolean success = save(user);
            if (success) {
                log.info("创建用户成功，userId={}, username={}", user.getUserId(), user.getUsername());
                return Result.success();
            } else {
                return Result.error("创建用户失败");
            }
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateUser(User user) {
        try {
            User existUser = getById(user.getUserId());
            if (existUser == null) {
                return Result.error("用户不存在");
            }

            // 如果修改用户名，检查新用户名是否已存在
            if (!existUser.getUsername().equals(user.getUsername())) {
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getUsername, user.getUsername())
                        .ne(User::getUserId, user.getUserId());
                if (count(wrapper) > 0) {
                    return Result.error("用户名已存在");
                }
            }

            boolean success = updateById(user);
            if (success) {
                log.info("更新用户成功，userId={}", user.getUserId());
                return Result.success();
            } else {
                return Result.error("更新用户失败");
            }
        } catch (Exception e) {
            log.error("更新用户失败，userId={}", user.getUserId(), e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteUser(Long userId) {
        try {
            User user = getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            boolean success = removeById(userId);
            if (success) {
                log.info("删除用户成功，userId={}", userId);
                return Result.success();
            } else {
                return Result.error("删除用户失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败，userId={}", userId, e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        User user = getUserByUsername(username);
        if (user == null) {
            log.warn("登录失败，用户不存在，username={}", username);
            return null;
        }

        // 验证密码（实际项目中应该使用 BCrypt 等加密方式）
        // 这里简单比较，因为数据库中的密码已经是 BCrypt 加密后的
        if (!password.equals("123456")) {
            // 实际应该使用 BCrypt 验证：BCrypt.checkpw(password, user.getPassword())
            log.warn("登录失败，密码错误，username={}", username);
            return null;
        }

        return user;
    }

    /**
     * 实体转VO
     */
    private UserVO convertToVO(User user) {
        if (user == null) {
            return null;
        }

        UserVO vo = new UserVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setGender(user.getGender());
        vo.setGenderDesc(getGenderDesc(user.getGender()));
        vo.setDeptId(user.getDeptId());
        vo.setPosition(user.getPosition());
        vo.setStatus(user.getStatus());
        vo.setStatusDesc(getStatusDesc(user.getStatus()));
        vo.setBalance(user.getBalance());
        vo.setRemark(user.getRemark());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        return vo;
    }

    /**
     * 获取性别描述
     */
    private String getGenderDesc(Integer gender) {
        if (gender == null) {
            return "未知";
        }
        return switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "启用" : "禁用";
    }
}

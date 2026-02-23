package com.eagleeye.system.controller;

import com.eagleeye.common.result.Result;
import com.eagleeye.system.dto.UserQueryDTO;
import com.eagleeye.system.entity.User;
import com.eagleeye.system.service.UserService;
import com.eagleeye.system.vo.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户CRUD操作")
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<IPage<UserVO>> pageUser(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            UserQueryDTO queryDTO) {

        log.info("分页查询用户列表，current={}, size={}, queryDTO={}", current, size, queryDTO);
        return userService.pageUser(current, size, queryDTO);
    }

    @Operation(summary = "根据ID查询用户详情")
    @GetMapping("/{userId}")
    public Result<UserVO> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long userId) {

        log.info("查询用户详情，userId={}", userId);
        return userService.getUserById(userId);
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Void> createUser(@RequestBody User user) {
        log.info("创建用户，user={}", user);
        return userService.createUser(user);
    }

    @Operation(summary = "更新用户")
    @PutMapping
    public Result<Void> updateUser(@RequestBody User user) {
        log.info("更新用户，user={}", user);
        return userService.updateUser(user);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {

        log.info("删除用户，userId={}", userId);
        return userService.deleteUser(userId);
    }
}

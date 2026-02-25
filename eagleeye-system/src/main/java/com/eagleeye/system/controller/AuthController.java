package com.eagleeye.system.controller;

import com.eagleeye.common.result.Result;
import com.eagleeye.common.util.JwtUtil;
import com.eagleeye.system.dto.LoginDTO;
import com.eagleeye.system.entity.User;
import com.eagleeye.system.service.UserService;
import com.eagleeye.system.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证 Controller
 * 登录、登出等接口
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "认证管理", description = "登录、登出等认证相关接口")
@SecurityRequirements() // 不需要认证
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @return 登录响应（包含 Token）
     */
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，成功返回 JWT Token")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        log.info("用户登录请求，username={}", loginDTO.getUsername());

        // 1. 验证用户名密码
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        // 2. 检查用户状态
        if (user.getStatus() != 1) {
            return Result.error("用户已被禁用");
        }

        // 3. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());

        // 4. 构建响应
        LoginVO.UserInfoVO userInfo = LoginVO.UserInfoVO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .deptId(user.getDeptId())
                .position(user.getPosition())
                .build();

        LoginVO loginVO = LoginVO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expiration / 1000)  // 转换为秒
                .userInfo(userInfo)
                .build();

        log.info("用户登录成功，userId={}, username={}", user.getUserId(), user.getUsername());
        return Result.success(loginVO);
    }

    /**
     * 刷新 Token
     *
     * @param token 旧 Token
     * @return 新 Token
     */
    @Operation(summary = "刷新 Token", description = "使用有效的 Token 刷新获取新 Token")
    @PostMapping("/refresh")
    public Result<LoginVO> refreshToken(@RequestHeader("Authorization") String token) {
        // 去掉 Bearer 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            return Result.error("Token 无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);

        // 生成新 Token
        String newToken = jwtUtil.generateToken(userId, username);

        // 获取用户信息
        User user = userService.getById(userId);

        LoginVO.UserInfoVO userInfo = LoginVO.UserInfoVO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .deptId(user.getDeptId())
                .position(user.getPosition())
                .build();

        LoginVO loginVO = LoginVO.builder()
                .accessToken(newToken)
                .tokenType("Bearer")
                .expiresIn(expiration / 1000)
                .userInfo(userInfo)
                .build();

        log.info("Token 刷新成功，userId={}", userId);
        return Result.success(loginVO);
    }
}

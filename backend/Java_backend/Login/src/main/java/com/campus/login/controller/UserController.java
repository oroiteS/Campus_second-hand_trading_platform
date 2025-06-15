package com.campus.login.controller;

import com.campus.login.common.Result;
import com.campus.login.dto.LoginRequest;
import com.campus.login.dto.RegisterRequest;
import com.campus.login.entity.User;
import com.campus.login.service.UserService;
import com.campus.login.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("用户注册请求，用户名: {}", registerRequest.getUserName());
        
        Map<String, Object> result = userService.register(registerRequest);
        
        if ((Boolean) result.get("success")) {
            return Result.success("注册成功", result);
        } else {
            return Result.badRequest((String) result.get("message"));
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求，用户名: {}", loginRequest.getUsername());
        
        Map<String, Object> result = userService.login(loginRequest);
        
        if ((Boolean) result.get("success")) {
            return Result.success("登录成功", result);
        } else {
            return Result.unauthorized((String) result.get("message"));
        }
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 验证token
            if (!jwtUtil.isValidToken(token)) {
                return Result.unauthorized("无效的令牌");
            }
            
            // 获取用户ID
            String userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getUserById(userId);
            
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 清除敏感信息
            user.setPassword(null);
            user.setIdCard(null);
            
            return Result.success(user);
            
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }
    
    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.isUserNameExists(username);
        return Result.success(!exists); // 返回是否可用（不存在即可用）
    }
    
    /**
     * 检查电话号码是否可用
     */
    @GetMapping("/check-telephone")
    public Result<Boolean> checkTelephone(@RequestParam String telephone) {
        boolean exists = userService.isTelephoneExists(telephone);
        return Result.success(!exists); // 返回是否可用（不存在即可用）
    }
    
    /**
     * 检查身份证号是否可用
     */
    @GetMapping("/check-idcard")
    public Result<Boolean> checkIdCard(@RequestParam String idCard) {
        boolean exists = userService.isIdCardExists(idCard);
        return Result.success(!exists); // 返回是否可用（不存在即可用）
    }
    
    /**
     * 验证令牌
     */
    @PostMapping("/validate-token")
    public Result<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            boolean isValid = jwtUtil.isValidToken(token);
            return Result.success(isValid);
            
        } catch (Exception e) {
            return Result.success(false);
        }
    }
}
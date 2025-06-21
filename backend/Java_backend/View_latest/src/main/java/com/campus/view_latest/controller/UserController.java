package com.campus.view_latest.controller;

import com.campus.view_latest.common.Result;
import com.campus.view_latest.entity.User;
import com.campus.view_latest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api-8087/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "用户管理", description = "用户相关API")
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取所有用户的指定字段
     * @return 用户列表
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有用户信息", description = "获取所有用户的User_ID、User_name、telephone、Create_at、User_sta和avatar_url字段")
    public Result<List<User>> getAllUsersWithSpecificFields() {
        log.info("接收到获取所有用户信息的请求");
        try {
            List<User> users = userService.getAllUsersWithSpecificFields();
            return Result.success("获取用户信息成功", users);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
}
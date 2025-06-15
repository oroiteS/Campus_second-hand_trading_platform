package com.campus.profile.controller;

import com.campus.profile.dto.UserInfoRequest;
import com.campus.profile.pojo.User;
import com.campus.profile.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求
 */
@Tag(name = "用户信息管理", description = "用户信息查询相关接口")
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查看用户信息
     * 根据请求体中的用户ID获取用户的所有信息并返回JSON格式
     * 
     * @param request 包含用户ID的请求体
     * @return 用户信息的JSON响应
     */
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息（脱敏处理）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(
            @Parameter(description = "用户信息请求体，包含用户ID", required = true)
            @Valid @RequestBody UserInfoRequest request) {
        try {
            // 从请求体中获取用户ID
            String userId = request.getUserId();
            
            // 通过服务层获取用户信息
            User user = userService.getUserById(userId);
            
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户不存在");
                errorResponse.put("code", 404);
                return ResponseEntity.status(404).body(errorResponse);
            }

            // 构建用户信息响应（排除敏感信息）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("userName", user.getUserName());
            userInfo.put("telephone", user.getMaskedTelephone()); // 使用脱敏电话
            userInfo.put("realName", user.getRealName());
            userInfo.put("avatarUrl", user.getAvatarUrl());
            userInfo.put("userLocLongitude", user.getUserLocLongitude());
            userInfo.put("userLocLatitude", user.getUserLocLatitude());
            userInfo.put("userStatus", user.getUserStatus());
            userInfo.put("createAt", user.getCreateAt());
            userInfo.put("idCard", user.getMaskedIdCard()); // 使用脱敏身份证
            userInfo.put("isBanned", user.isBanned());

            // 构建成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取用户信息成功");
            response.put("code", 200);
            response.put("data", userInfo);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // 异常处理
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 获取用户完整信息（管理员接口）
     * 返回包含敏感信息的完整用户数据
     * 
     * @param request 包含用户ID的请求体
     * @return 完整用户信息的JSON响应
     */
    @Operation(summary = "获取用户完整信息", description = "管理员接口：获取包含敏感信息的完整用户数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户完整信息"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/admin/info")
    public ResponseEntity<Map<String, Object>> getFullUserInfo(
            @Parameter(description = "用户信息请求体，包含用户ID", required = true)
            @Valid @RequestBody UserInfoRequest request) {
        try {
            // 从请求体中获取用户ID
            String userId = request.getUserId();
            
            User user = userService.getUserById(userId);
            
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户不存在");
                errorResponse.put("code", 404);
                return ResponseEntity.status(404).body(errorResponse);
            }

            // 构建完整用户信息响应（包含敏感信息，仅供管理员使用）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("userName", user.getUserName());
            userInfo.put("telephone", user.getTelephone()); // 完整电话号码
            userInfo.put("realName", user.getRealName());
            userInfo.put("avatarUrl", user.getAvatarUrl());
            userInfo.put("userLocLongitude", user.getUserLocLongitude());
            userInfo.put("userLocLatitude", user.getUserLocLatitude());
            userInfo.put("userStatus", user.getUserStatus());
            userInfo.put("createAt", user.getCreateAt());
            userInfo.put("idCard", user.getIdCard()); // 完整身份证号
            userInfo.put("isBanned", user.isBanned());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取用户完整信息成功");
            response.put("code", 200);
            response.put("data", userInfo);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 获取用户基本信息（公开接口）
     * 只返回非敏感的基本信息
     * 
     * @param request 包含用户ID的请求体
     * @return 基本用户信息的JSON响应
     */
    @Operation(summary = "获取用户基本信息", description = "公开接口：获取用户基本信息（仅包含非敏感数据）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户基本信息"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/basic")
    public ResponseEntity<Map<String, Object>> getBasicUserInfo(
            @Parameter(description = "用户信息请求体，包含用户ID", required = true)
            @Valid @RequestBody UserInfoRequest request) {
        try {
            // 从请求体中获取用户ID
            String userId = request.getUserId();
            
            User user = userService.getUserById(userId);
            
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户不存在");
                errorResponse.put("code", 404);
                return ResponseEntity.status(404).body(errorResponse);
            }

            // 构建基本用户信息响应（仅公开信息）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("userName", user.getUserName());
            userInfo.put("avatarUrl", user.getAvatarUrl());
            userInfo.put("createAt", user.getCreateAt());
            userInfo.put("isBanned", user.isBanned());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取用户基本信息成功");
            response.put("code", 200);
            response.put("data", userInfo);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
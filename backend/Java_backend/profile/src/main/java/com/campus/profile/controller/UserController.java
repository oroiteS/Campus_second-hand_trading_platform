package com.campus.profile.controller;

import com.campus.profile.dto.UserInfoRequest;
import com.campus.profile.dto.UpdateUserRequest;
import com.campus.profile.dto.UpdateAvatarRequest;
import com.campus.profile.pojo.User;
import com.campus.profile.service.UserService;
import com.campus.profile.util.FileUploadUtil;
import org.springframework.web.multipart.MultipartFile;
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
    
    @Autowired
    private FileUploadUtil fileUploadUtil;

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

    /**
     * 更新用户信息
     * 支持修改用户名和电话号码
     * 
     * @param request 包含用户ID、用户名和电话号码的请求体
     * @return 更新结果的JSON响应
     */
    @Operation(summary = "更新用户信息", description = "更新用户的用户名和电话号码")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户信息更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "409", description = "用户名或电话号码已被使用"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUserInfo(
            @Parameter(description = "用户信息更新请求体，包含用户ID、用户名和电话号码", required = true)
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            // 从请求体中获取参数
            String userId = request.getUserId();
            String userName = request.getUserName();
            String telephone = request.getTelephone();
            
            // 调用服务层更新用户信息
            boolean updateResult = userService.updateUserInfo(userId, userName, telephone);
            
            if (updateResult) {
                // 更新成功，返回成功响应
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "用户信息更新成功");
                response.put("code", 200);
                
                // 返回更新后的用户信息
                User updatedUser = userService.getUserById(userId);
                if (updatedUser != null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("userId", updatedUser.getUserId());
                    userInfo.put("userName", updatedUser.getUserName());
                    userInfo.put("telephone", updatedUser.getMaskedTelephone());
                    response.put("data", userInfo);
                }
                
                return ResponseEntity.ok(response);
            } else {
                // 更新失败
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户信息更新失败");
                errorResponse.put("code", 500);
                return ResponseEntity.status(500).body(errorResponse);
            }
            
        } catch (IllegalArgumentException e) {
            // 参数验证失败或业务逻辑错误
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            // 根据错误信息设置不同的状态码
            if (e.getMessage().contains("用户不存在")) {
                errorResponse.put("code", 404);
                return ResponseEntity.status(404).body(errorResponse);
            } else if (e.getMessage().contains("已被") || e.getMessage().contains("已使用")) {
                errorResponse.put("code", 409);
                return ResponseEntity.status(409).body(errorResponse);
            } else {
                errorResponse.put("code", 400);
                return ResponseEntity.status(400).body(errorResponse);
            }
            
        } catch (Exception e) {
            // 其他异常
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
         }
     }

    /**
     * 上传用户头像
     * 支持JPG、PNG、GIF、WEBP格式，最大5MB
     * 
     * @param file 头像文件
     * @param userId 用户ID
     * @return 上传结果的JSON响应
     */
    @Operation(summary = "上传用户头像", description = "上传用户头像文件，支持JPG、PNG、GIF、WEBP格式，最大5MB")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "头像上传成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误或文件格式不支持"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/avatar/upload")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @Parameter(description = "头像文件，支持JPG、PNG、GIF、WEBP格式，最大5MB", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "用户ID", required = true)
            @RequestParam("userId") String userId) {
        try {
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户ID不能为空");
                errorResponse.put("code", 400);
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // 获取用户当前头像URL（用于删除旧文件）
            User currentUser = userService.getUserById(userId.trim());
            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户不存在");
                errorResponse.put("code", 404);
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            String oldAvatarUrl = currentUser.getAvatarUrl();
            
            // 上传新头像文件
            String newAvatarUrl = fileUploadUtil.uploadAvatar(file, userId.trim());
            
            // 更新数据库中的头像URL
            boolean updateResult = userService.updateUserAvatar(userId.trim(), newAvatarUrl);
            
            if (updateResult) {
                // 删除旧头像文件（如果存在）
                if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                    fileUploadUtil.deleteOldAvatar(oldAvatarUrl);
                }
                
                // 返回成功响应
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "头像上传成功");
                response.put("code", 200);
                
                Map<String, Object> data = new HashMap<>();
                data.put("userId", userId.trim());
                data.put("avatarUrl", newAvatarUrl);
                response.put("data", data);
                
                return ResponseEntity.ok(response);
            } else {
                // 数据库更新失败，删除已上传的文件
                fileUploadUtil.deleteOldAvatar(newAvatarUrl);
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "头像上传失败，数据库更新错误");
                errorResponse.put("code", 500);
                return ResponseEntity.status(500).body(errorResponse);
            }
            
        } catch (IllegalArgumentException e) {
            // 文件验证失败或业务逻辑错误
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("code", 400);
            return ResponseEntity.status(400).body(errorResponse);
            
        } catch (Exception e) {
            // 其他异常
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 更新用户头像URL
     * 用于直接更新头像URL（不上传文件）
     * 
     * @param request 包含用户ID和头像URL的请求体
     * @return 更新结果的JSON响应
     */
    @Operation(summary = "更新用户头像URL", description = "直接更新用户头像URL（不上传文件）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "头像URL更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/avatar/update")
    public ResponseEntity<Map<String, Object>> updateAvatarUrl(
            @Parameter(description = "头像更新请求体，包含用户ID和头像URL", required = true)
            @Valid @RequestBody UpdateAvatarRequest request) {
        try {
            // 从请求体中获取参数
            String userId = request.getUserId();
            String avatarUrl = request.getAvatarUrl();
            
            // 调用服务层更新头像URL
            boolean updateResult = userService.updateUserAvatar(userId, avatarUrl);
            
            if (updateResult) {
                // 更新成功，返回成功响应
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "头像URL更新成功");
                response.put("code", 200);
                
                // 返回更新后的用户信息
                User updatedUser = userService.getUserById(userId);
                if (updatedUser != null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("userId", updatedUser.getUserId());
                    userInfo.put("userName", updatedUser.getUserName());
                    userInfo.put("avatarUrl", updatedUser.getAvatarUrl());
                    response.put("data", userInfo);
                }
                
                return ResponseEntity.ok(response);
            } else {
                // 更新失败
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "头像URL更新失败");
                errorResponse.put("code", 500);
                return ResponseEntity.status(500).body(errorResponse);
            }
            
        } catch (IllegalArgumentException e) {
            // 参数验证失败或业务逻辑错误
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            // 根据错误信息设置不同的状态码
            if (e.getMessage().contains("用户不存在")) {
                errorResponse.put("code", 404);
                return ResponseEntity.status(404).body(errorResponse);
            } else {
                errorResponse.put("code", 400);
                return ResponseEntity.status(400).body(errorResponse);
            }
            
        } catch (Exception e) {
            // 其他异常
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
 }
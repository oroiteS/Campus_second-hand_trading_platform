package com.campus.profile.controller;

import com.campus.profile.dto.UserInfoRequest;
import com.campus.profile.dto.UpdateUserRequest;
import com.campus.profile.dto.UpdateAvatarRequest;
import com.campus.profile.pojo.User;
import com.campus.profile.service.UserService;
import com.campus.profile.util.FileUploadUtil;
import com.campus.profile.util.OssUtil;
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
import com.campus.profile.dto.ChangePasswordRequest; // 新增导入
import java.nio.charset.StandardCharsets; // 新增导入
import java.security.MessageDigest; // 新增导入
import java.security.NoSuchAlgorithmException; // 新增导入


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
    private UserService UserService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    @Autowired
    private OssUtil ossUtil;

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
            User user = UserService.getUserById(userId);
            
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
            userInfo.put("telephone", user.getTelephone()); // 不使用脱敏电话
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
            
            User user = UserService.getUserById(userId);
            
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
            
            User user = UserService.getUserById(userId);
            
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
            boolean updateResult = UserService.updateUserInfo(userId, userName, telephone);
            
            if (updateResult) {
                // 更新成功，返回成功响应
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "用户信息更新成功");
                response.put("code", 200);
                
                // 返回更新后的用户信息
                User updatedUser = UserService.getUserById(userId);
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
     * 上传用户头像到OSS
     * 支持JPG、PNG、GIF、WEBP格式，最大5MB
     * 
     * @param file 头像文件
     * @param userId 用户ID
     * @return 上传结果的JSON响应
     */
    @Operation(summary = "上传用户头像", description = "上传用户头像文件到阿里云OSS，支持JPG、PNG、GIF、WEBP格式，最大5MB")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "头像上传成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误或文件格式不支持"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/avatar/upload", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @Parameter(description = "头像文件，支持JPG、PNG、GIF、WEBP格式，最大5MB", required = true)
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "用户ID", required = true)
            @RequestPart("userId") String userId) {
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
            User currentUser = UserService.getUserById(userId.trim());
            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户不存在");
                errorResponse.put("code", 404);
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            String oldAvatarUrl = currentUser.getAvatarUrl();
            
            // 上传新头像文件到OSS
            String fileKey = ossUtil.uploadAvatar(file, userId.trim());
            
            // 生成公共访问URL（永久有效）
            String fullAvatarUrl = ossUtil.generatePublicUrl(fileKey);
            fullAvatarUrl = "https://"+fullAvatarUrl;
            
            // 如果生成URL失败，删除已上传的文件并返回错误
            if (fullAvatarUrl == null) {
                ossUtil.deleteFile(fileKey);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "生成访问URL失败");
                errorResponse.put("code", 500);
                return ResponseEntity.status(500).body(errorResponse);
            }
            
            // 更新数据库中的头像URL（存储完整的访问URL）
            boolean updateResult = UserService.updateUserAvatar(userId.trim(), fullAvatarUrl);
            
            if (updateResult) {
                // 删除旧头像文件（如果存在）
                if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                    String oldFileKey = ossUtil.extractFileKeyFromUrl(oldAvatarUrl);
                    ossUtil.deleteFile(oldFileKey);
                }
                
                // 返回成功响应
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", "头像上传成功");
                successResponse.put("code", 200);
                successResponse.put("data", Map.of(
                    "avatarUrl", fullAvatarUrl,
                    "fileKey", fileKey,
                    "userId", userId.trim()
                ));
                return ResponseEntity.ok(successResponse);
            } else {
                // 数据库更新失败，删除已上传的文件
                ossUtil.deleteFile(fileKey);
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "更新用户头像失败");
                errorResponse.put("code", 500);
                return ResponseEntity.status(500).body(errorResponse);
            }
            
        } catch (IllegalArgumentException e) {
            // 文件验证失败
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
     * 获取头像访问URL（使用JSON请求体）
     * 
     * @param request 包含用户ID的请求体
     * @return 头像访问URL
     */
    @Operation(summary = "获取头像访问URL", description = "通过JSON请求体传入用户ID，获取用户头像的访问URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "用户不存在或头像不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/avatar/url")
    public ResponseEntity<Map<String, Object>> getAvatarUrl(
            @Parameter(description = "用户信息请求体，包含用户ID", required = true)
            @Valid @RequestBody UserInfoRequest request) {
        try {
            // 从请求体中获取用户ID
            String userId = request.getUserId();
            
            // 获取用户信息
            User user = UserService.getUserById(userId);
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户不存在");
                errorResponse.put("code", 404);
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            // 获取数据库中的头像URL
            String avatarUrl = user.getAvatarUrl();
            if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户未设置头像");
                errorResponse.put("code", 404);
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            // 返回成功响应
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "获取头像URL成功");
            successResponse.put("code", 200);
            successResponse.put("data", Map.of(
                "userId", userId,
                "avatarUrl", avatarUrl.trim()
            ));
            return ResponseEntity.ok(successResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            errorResponse.put("code", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }


    private static String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }

    @Operation(summary = "修改用户密码", description = "根据用户ID、原密码和新密码修改用户密码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密码修改成功"),
            @ApiResponse(responseCode = "400", description = "请求参数无效或原密码错误"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/password/change")
    public ResponseEntity<Map<String, Object>> changePassword(
            @Parameter(description = "修改密码请求体", required = true)
            @Valid @RequestBody ChangePasswordRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userId = request.getUserId();
            String oldPassword = request.getOldPassword();
            String newPassword = request.getNewPassword();

            User user = UserService.getUserById(userId);

            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                response.put("code", 404);
                return ResponseEntity.status(404).body(response);
            }

            // 加密传入的原密码和新密码
            String encryptedOldPassword = encryptPassword(oldPassword);
            String encryptedNewPassword = encryptPassword(newPassword);

            // 验证原密码
            if (!user.getPassword().equals(encryptedOldPassword)) {
                response.put("success", false);
                response.put("message", "原密码错误");
                response.put("code", 400); // 或者使用更具体的错误码，例如401表示未授权（密码错误）
                return ResponseEntity.status(400).body(response);
            }

            // 更新密码
            user.setPassword(encryptedNewPassword);
            User updatedUser = UserService.updateUser(user); // 假设userService有updateUser方法来更新整个用户信息
            // 或者可以创建一个专门的updatePassword方法

            if (updatedUser != null) {
                response.put("success", true);
                response.put("message", "密码修改成功");
                response.put("code", 200);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "密码修改失败，请稍后重试");
                response.put("code", 500);
                return ResponseEntity.status(500).body(response);
            }

        } catch (RuntimeException e) {
            // 处理 encryptPassword 可能抛出的 RuntimeException
            response.put("success", false);
            response.put("message", "密码加密失败: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器内部错误: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        }
    }


   @Operation(summary = "重置用户密码", description = "根据用户ID将用户密码重置为默认密码123456")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "密码重置成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @Parameter(description = "用户信息请求体，包含用户ID", required = true)
            @Valid @RequestBody UserInfoRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userId = request.getUserId();

            User user = UserService.getUserById(userId);

            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                response.put("code", 404);
                return ResponseEntity.status(404).body(response);
            }

            // 将密码重置为默认密码123456，并进行SHA-256加密
            String defaultPassword = "123456";
            String encryptedDefaultPassword = encryptPassword(defaultPassword);

            // 更新密码
            user.setPassword(encryptedDefaultPassword);
            User updatedUser = UserService.updateUser(user);

            if (updatedUser != null) {
                response.put("success", true);
                response.put("message", "密码重置成功，新密码为：123456");
                response.put("code", 200);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "密码重置失败，请稍后重试");
                response.put("code", 500);
                return ResponseEntity.status(500).body(response);
            }

        } catch (RuntimeException e) {
            // 处理 encryptPassword 可能抛出的 RuntimeException
            response.put("success", false);
            response.put("message", "密码加密失败: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器内部错误: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        }
    }
 }
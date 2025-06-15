package com.campus.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户信息请求DTO
 * 用于接收包含用户ID的JSON请求体
 */
@Schema(description = "用户信息请求体")
public class UserInfoRequest {
    
    @Schema(description = "用户ID", example = "U10000001", required = true)
    @NotNull(message = "用户ID不能为空")
    @NotBlank(message = "用户ID不能为空字符串")
    private String userId;
    
    public UserInfoRequest() {
    }
    
    public UserInfoRequest(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "UserInfoRequest{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
package com.campus.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新头像请求DTO
 * 用于接收头像更新请求的参数
 */
@Schema(description = "更新头像请求体")
public class UpdateAvatarRequest {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "12345", required = true)
    @NotBlank(message = "用户ID不能为空")
    @Size(max = 9, message = "用户ID长度不能超过9位")
    private String userId;

    /**
     * 头像URL（文件上传后的服务器路径）
     */
    @Schema(description = "头像URL", example = "/uploads/avatars/12345_avatar.jpg")
    @Size(max = 255, message = "头像URL长度不能超过255位")
    private String avatarUrl;

    // 默认构造函数
    public UpdateAvatarRequest() {}

    // 带参构造函数
    public UpdateAvatarRequest(String userId, String avatarUrl) {
        this.userId = userId;
        this.avatarUrl = avatarUrl;
    }

    // Getter和Setter方法
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "UpdateAvatarRequest{" +
                "userId='" + userId + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
package com.campus.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户信息修改请求DTO
 * 用于接收包含用户ID、用户名和电话号码的JSON请求体
 */
@Schema(description = "用户信息修改请求体")
public class UpdateUserRequest {
    
    @Schema(description = "用户ID", example = "U10000001", required = true)
    @NotNull(message = "用户ID不能为空")
    @NotBlank(message = "用户ID不能为空字符串")
    private String userId;
    
    @Schema(description = "用户名", example = "张三", required = true)
    @NotNull(message = "用户名不能为空")
    @NotBlank(message = "用户名不能为空字符串")
    private String userName;
    
    @Schema(description = "电话号码", example = "13812345678", required = true)
    @NotNull(message = "电话号码不能为空")
    @NotBlank(message = "电话号码不能为空字符串")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "电话号码格式不正确")
    private String telephone;
    
    public UpdateUserRequest() {
    }
    
    public UpdateUserRequest(String userId, String userName, String telephone) {
        this.userId = userId;
        this.userName = userName;
        this.telephone = telephone;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}
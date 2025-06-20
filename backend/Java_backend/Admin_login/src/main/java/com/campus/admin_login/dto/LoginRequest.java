package com.campus.admin_login.dto;
/*
*@belongsPackage: com.campus.admin_login.dto
*@author: syn
*@createTime: 2025-06-20
*@description: 登录请求类
*@version: 1.0
*/
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "管理员ID不能为空")
    private String rootId;

    @NotBlank(message = "密码不能为空")
    private String password;
}
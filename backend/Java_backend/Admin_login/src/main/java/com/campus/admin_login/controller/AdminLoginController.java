package com.campus.admin_login.controller;
/*
*@belongsPackage: com.campus.admin_login.controller
*@author: syn
*@createTime: 2025-06-20
*@description: 
*@version: 1.0
*/
import com.campus.admin_login.dto.ApiResponse;
import com.campus.admin_login.dto.LoginRequest;
import com.campus.admin_login.service.AdminLoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminLoginController {

    @Autowired
    private AdminLoginService adminLoginService;

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        boolean success = adminLoginService.login(loginRequest);

        if (success) {
            return ApiResponse.success("登录成功");
        } else {
            return ApiResponse.error("用户名或密码错误");
        }
    }
}

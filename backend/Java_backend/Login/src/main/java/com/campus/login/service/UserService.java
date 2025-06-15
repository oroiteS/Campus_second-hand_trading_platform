package com.campus.login.service;

import com.campus.login.dto.LoginRequest;
import com.campus.login.dto.RegisterRequest;
import com.campus.login.entity.User;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    Map<String, Object> register(RegisterRequest registerRequest);
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录结果（包含token）
     */
    Map<String, Object> login(LoginRequest loginRequest);
    
    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(String userId);
    
    /**
     * 检查用户名是否存在
     * @param userName 用户名
     * @return 是否存在
     */
    boolean isUserNameExists(String userName);
    
    /**
     * 检查电话号码是否存在
     * @param telephone 电话号码
     * @return 是否存在
     */
    boolean isTelephoneExists(String telephone);
    
    /**
     * 检查身份证号是否存在
     * @param idCard 身份证号
     * @return 是否存在
     */
    boolean isIdCardExists(String idCard);
}
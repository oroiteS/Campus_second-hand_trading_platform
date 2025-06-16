package com.campus.login.service.impl;

import com.campus.login.dto.LoginRequest;
import com.campus.login.dto.RegisterRequest;
import com.campus.login.entity.User;
import com.campus.login.mapper.UserMapper;
import com.campus.login.service.UserService;
import com.campus.login.util.JwtUtil;
import com.campus.login.util.PasswordUtil;
import com.campus.login.util.UserIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    
    @Override
    @Transactional
    public Map<String, Object> register(RegisterRequest registerRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查用户名是否已存在
            if (isUserNameExists(registerRequest.getUserName())) {
                result.put("success", false);
                result.put("message", "用户名已存在");
                return result;
            }
            
            // 检查电话号码是否已存在
            if (isTelephoneExists(registerRequest.getTelephone())) {
                result.put("success", false);
                result.put("message", "电话号码已被注册");
                return result;
            }
            
            // 检查身份证号是否已存在
            if (isIdCardExists(registerRequest.getIdCard())) {
                result.put("success", false);
                result.put("message", "身份证号已被注册");
                return result;
            }
            
            // 检查用户ID是否已存在
            if (getUserById(registerRequest.getUserId()) != null) {
                result.put("success", false);
                result.put("message", "用户ID已存在");
                return result;
            }
            
            // 创建用户对象
            User user = new User();
            user.setUserId(registerRequest.getUserId());
            user.setUserName(registerRequest.getUserName());
            user.setPassword(PasswordUtil.encryptPassword(registerRequest.getPassword()));
            user.setTelephone(registerRequest.getTelephone());
            user.setRealName(registerRequest.getRealName());
            user.setIdCard(registerRequest.getIdCard());

            user.setUserSta(false); // 默认未封号
            user.setCreateAt(LocalDateTime.now());
            
            // 保存用户
            int insertResult = userMapper.insert(user);
            
            if (insertResult > 0) {
                result.put("success", true);
                result.put("message", "注册成功");
                result.put("userId", registerRequest.getUserId());
                log.info("用户注册成功，用户ID: {}, 用户名: {}", registerRequest.getUserId(), registerRequest.getUserName());
            } else {
                result.put("success", false);
                result.put("message", "注册失败，请重试");
            }
            
        } catch (Exception e) {
            log.error("用户注册失败", e);
            result.put("success", false);
            result.put("message", "注册失败：" + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> login(LoginRequest loginRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 根据用户名或电话号码查询用户
            User user = userMapper.findByUsernameOrTelephone(loginRequest.getUsername());
            
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            // 检查用户是否被封号
            if (user.getUserSta()) {
                result.put("success", false);
                result.put("message", "账户已被封禁，请联系管理员");
                return result;
            }
            
            // 验证密码
            if (!PasswordUtil.matches(loginRequest.getPassword(), user.getPassword())) {
                result.put("success", false);
                result.put("message", "密码错误");
                return result;
            }
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUserId(), user.getUserName());
            
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("token", token);
            result.put("userId", user.getUserId());
            result.put("userName", user.getUserName());
            result.put("realName", user.getRealName());
            result.put("avatarUrl", user.getAvatarUrl());
            
            log.info("用户登录成功，用户ID: {}, 用户名: {}", user.getUserId(), user.getUserName());
            
        } catch (Exception e) {
            log.error("用户登录失败", e);
            result.put("success", false);
            result.put("message", "登录失败：" + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public User getUserById(String userId) {
        try {
            return userMapper.selectById(userId);
        } catch (Exception e) {
            log.error("查询用户失败，用户ID: {}", userId, e);
            return null;
        }
    }
    
    @Override
    public boolean isUserNameExists(String userName) {
        try {
            User user = userMapper.findByUserName(userName);
            return user != null;
        } catch (Exception e) {
            log.error("检查用户名是否存在失败，用户名: {}", userName, e);
            return false;
        }
    }
    
    @Override
    public boolean isTelephoneExists(String telephone) {
        try {
            User user = userMapper.findByTelephone(telephone);
            return user != null;
        } catch (Exception e) {
            log.error("检查电话号码是否存在失败，电话号码: {}", telephone, e);
            return false;
        }
    }
    
    @Override
    public boolean isIdCardExists(String idCard) {
        try {
            User user = userMapper.findByIdCard(idCard);
            return user != null;
        } catch (Exception e) {
            log.error("检查身份证号是否存在失败，身份证号: {}", idCard, e);
            return false;
        }
    }
}
package com.campus.view_latest.service.impl;

import com.campus.view_latest.entity.User;
import com.campus.view_latest.mapper.UserMapper;
import com.campus.view_latest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    
    @Override
    public List<User> getAllUsersWithSpecificFields() {
        log.info("获取所有用户的指定字段信息");
        try {
            List<User> users = userMapper.selectAllUsersWithSpecificFields();
            log.info("成功获取{}个用户信息", users.size());
            return users;
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            throw new RuntimeException("获取用户信息失败", e);
        }
    }
}
package com.campus.view_latest.service;

import com.campus.view_latest.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取所有用户的指定字段
     * @return 用户列表
     */
    List<User> getAllUsersWithSpecificFields();
}
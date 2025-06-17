package com.campus.ban.service;

import com.campus.ban.entity.User;

/**
 * 封号服务接口
 */
public interface BanService {
    
    /**
     * 根据用户ID封号用户
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean banUserById(String userId);
    
    /**
     * 根据用户ID解封用户
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean unbanUserById(String userId);
    
    /**
     * 根据用户名封号用户
     * @param userName 用户名
     * @return 操作结果
     */
    boolean banUserByName(String userName);
    
    /**
     * 根据用户名解封用户
     * @param userName 用户名
     * @return 操作结果
     */
    boolean unbanUserByName(String userName);
    
    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(String userId);
    
    /**
     * 根据用户名查询用户信息
     * @param userName 用户名
     * @return 用户信息
     */
    User getUserByName(String userName);
    
    /**
     * 检查用户是否存在
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean userExists(String userId);
    
    /**
     * 检查用户名是否存在
     * @param userName 用户名
     * @return 是否存在
     */
    boolean userNameExists(String userName);
}
package com.campus.profile.service;

import com.campus.profile.pojo.User;

/**
 * 用户服务接口
 * 定义用户相关的业务逻辑方法
 */
public interface UserService {

    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    User getUserById(String userId);

    /**
     * 根据用户名获取用户信息
     * 
     * @param userName 用户名
     * @return 用户对象，如果不存在则返回null
     */
    User getUserByUserName(String userName);

    /**
     * 根据电话号码获取用户信息
     * 
     * @param telephone 电话号码
     * @return 用户对象，如果不存在则返回null
     */
    User getUserByTelephone(String telephone);

    /**
     * 保存用户信息
     * 
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    User saveUser(User user);

    /**
     * 更新用户信息
     * 
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User updateUser(User user);

    /**
     * 删除用户
     * 
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(String userId);

    /**
     * 检查用户是否存在
     * 
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsById(String userId);

    /**
     * 检查用户名是否已存在
     * 
     * @param userName 用户名
     * @return 是否存在
     */
    boolean existsByUserName(String userName);

    /**
     * 检查电话号码是否已存在
     * 
     * @param telephone 电话号码
     * @return 是否存在
     */
    boolean existsByTelephone(String telephone);

    /**
     * 检查身份证号是否已存在
     * 
     * @param idCard 身份证号
     * @return 是否存在
     */
    boolean existsByIdCard(String idCard);
}
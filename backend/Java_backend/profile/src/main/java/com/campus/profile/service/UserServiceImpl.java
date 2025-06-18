package com.campus.profile.service;

import com.campus.profile.dao.UserRepository;
import com.campus.profile.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserById(String userId) {
        return userRepository.selectById(userId);
    }

    @Override
    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User getUserByTelephone(String telephone) {
        return userRepository.findByTelephone(telephone);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        userRepository.insert(user);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        userRepository.updateById(user);
        return user;
    }

    @Override
    @Transactional
    public boolean deleteUser(String userId) {
        userRepository.deleteById(userId);
        return false;
    }

    @Override
    public boolean existsById(String userId) {
        return userRepository.selectById(userId) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByUserName(userName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTelephone(String telephone) {
        if (telephone == null || telephone.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByTelephone(telephone);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByIdCard(idCard);
    }

    @Override
    @Transactional
    public boolean updateUserInfo(String userId, String userName, String telephone) {
        // 参数验证
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (telephone == null || telephone.trim().isEmpty()) {
            throw new IllegalArgumentException("电话号码不能为空");
        }

        // 检查用户是否存在
        if (!existsById(userId)) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 检查新用户名是否已被其他用户使用
        User existingUserByName = userRepository.findByUserName(userName);
        if (existingUserByName != null && !existingUserByName.getUserId().equals(userId)) {
            throw new IllegalArgumentException("用户名已被其他用户使用");
        }

        // 检查新电话号码是否已被其他用户使用
        User existingUserByPhone = userRepository.findByTelephone(telephone);
        if (existingUserByPhone != null && !existingUserByPhone.getUserId().equals(userId)) {
            throw new IllegalArgumentException("电话号码已被其他用户使用");
        }

        // 执行更新操作
        int result = userRepository.updateUserInfo(userId, userName, telephone);
        return result > 0;
    }

    @Override
    public boolean updateUserAvatar(String userId, String avatarUrl) {
        // 参数验证
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("头像URL不能为空");
        }

        // 检查用户是否存在
        User existingUser = userRepository.selectById(userId);
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 执行更新操作
        int result = userRepository.updateUserAvatar(userId, avatarUrl.trim());
        return result > 0;
    }
}
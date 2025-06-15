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
}
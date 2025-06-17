package com.campus.ban.service.impl;

import com.campus.ban.service.BanService;
import com.campus.ban.entity.User;
import com.campus.ban.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 封号服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BanServiceImpl implements BanService {
    
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public boolean banUserById(String userId) {
        try {
            // 检查用户是否存在
            User user = userMapper.findByUserId(userId);
            if (user == null) {
                log.warn("用户不存在，用户ID: {}", userId);
                return false;
            }
            
            // 检查用户是否已经被封号 (1表示封号，0表示正常)
            if (user.getUserSta() != null && user.getUserSta()) {
                log.warn("用户已被封号，用户ID: {}", userId);
                return false;
            }
            
            int result = userMapper.banUser(userId);
            if (result > 0) {
                log.info("用户封号成功，用户ID: {}, 用户名: {}", userId, user.getUserName());
                return true;
            } else {
                log.error("用户封号失败，用户ID: {}", userId);
                return false;
            }
        } catch (Exception e) {
            log.error("封号操作异常，用户ID: {}", userId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean unbanUserById(String userId) {
        try {
            // 检查用户是否存在
            User user = userMapper.findByUserId(userId);
            if (user == null) {
                log.warn("用户不存在，用户ID: {}", userId);
                return false;
            }
            
            // 检查用户是否已经解封
            if (user.getUserSta() != null && !user.getUserSta()) {
                log.warn("用户未被封号，用户ID: {}", userId);
                return false;
            }
            
            int result = userMapper.unbanUser(userId);
            if (result > 0) {
                log.info("用户解封成功，用户ID: {}, 用户名: {}", userId, user.getUserName());
                return true;
            } else {
                log.error("用户解封失败，用户ID: {}", userId);
                return false;
            }
        } catch (Exception e) {
            log.error("解封操作异常，用户ID: {}", userId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean banUserByName(String userName) {
        try {
            // 检查用户是否存在
            User user = userMapper.findByUserName(userName);
            if (user == null) {
                log.warn("用户不存在，用户名: {}", userName);
                return false;
            }
            
            // 检查用户是否已经被封号
            if (user.getUserSta() != null && user.getUserSta()) {
                log.warn("用户已被封号，用户名: {}", userName);
                return false;
            }
            
            int result = userMapper.banUserByName(userName);
            if (result > 0) {
                log.info("用户封号成功，用户ID: {}, 用户名: {}", user.getUserId(), userName);
                return true;
            } else {
                log.error("用户封号失败，用户名: {}", userName);
                return false;
            }
        } catch (Exception e) {
            log.error("封号操作异常，用户名: {}", userName, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean unbanUserByName(String userName) {
        try {
            // 检查用户是否存在
            User user = userMapper.findByUserName(userName);
            if (user == null) {
                log.warn("用户不存在，用户名: {}", userName);
                return false;
            }
            
            // 检查用户是否已经解封
            if (user.getUserSta() != null && !user.getUserSta()) {
                log.warn("用户未被封号，用户名: {}", userName);
                return false;
            }
            
            int result = userMapper.unbanUserByName(userName);
            if (result > 0) {
                log.info("用户解封成功，用户ID: {}, 用户名: {}", user.getUserId(), userName);
                return true;
            } else {
                log.error("用户解封失败，用户名: {}", userName);
                return false;
            }
        } catch (Exception e) {
            log.error("解封操作异常，用户名: {}", userName, e);
            return false;
        }
    }
    
    @Override
    public User getUserById(String userId) {
        try {
            return userMapper.findByUserId(userId);
        } catch (Exception e) {
            log.error("查询用户失败，用户ID: {}", userId, e);
            return null;
        }
    }
    
    @Override
    public User getUserByName(String userName) {
        try {
            return userMapper.findByUserName(userName);
        } catch (Exception e) {
            log.error("查询用户失败，用户名: {}", userName, e);
            return null;
        }
    }
    
    @Override
    public boolean userExists(String userId) {
        try {
            User user = userMapper.findByUserId(userId);
            return user != null;
        } catch (Exception e) {
            log.error("检查用户是否存在失败，用户ID: {}", userId, e);
            return false;
        }
    }
    
    @Override
    public boolean userNameExists(String userName) {
        try {
            User user = userMapper.findByUserName(userName);
            return user != null;
        } catch (Exception e) {
            log.error("检查用户名是否存在失败，用户名: {}", userName, e);
            return false;
        }
    }
}
package com.campus.ban.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.ban.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户ID查询用户
     */
    @Select("SELECT * FROM users WHERE User_ID = #{userId}")
    User findByUserId(String userId);
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE User_name = #{userName}")
    User findByUserName(String userName);
    
    /**
     * 封号用户
     */
    @Update("UPDATE users SET User_sta = 1 WHERE User_ID = #{userId}")
    int banUser(String userId);
    
    /**
     * 解封用户
     */
    @Update("UPDATE users SET User_sta = 0 WHERE User_ID = #{userId}")
    int unbanUser(String userId);
    
    /**
     * 根据用户名封号用户
     */
    @Update("UPDATE users SET User_sta = 1 WHERE User_name = #{userName}")
    int banUserByName(String userName);
    
    /**
     * 根据用户名解封用户
     */
    @Update("UPDATE users SET User_sta = 0 WHERE User_name = #{userName}")
    int unbanUserByName(String userName);
}
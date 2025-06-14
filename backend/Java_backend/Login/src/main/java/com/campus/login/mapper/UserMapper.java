package com.campus.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.login.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE User_name = #{userName}")
    User findByUserName(String userName);
    
    /**
     * 根据电话号码查询用户
     */
    @Select("SELECT * FROM users WHERE telephone = #{telephone}")
    User findByTelephone(String telephone);
    
    /**
     * 根据身份证号查询用户
     */
    @Select("SELECT * FROM users WHERE ID = #{idCard}")
    User findByIdCard(String idCard);
    
    /**
     * 根据用户名或电话号码查询用户（用于登录）
     */
    @Select("SELECT * FROM users WHERE User_name = #{username} OR telephone = #{username}")
    User findByUsernameOrTelephone(String username);
}
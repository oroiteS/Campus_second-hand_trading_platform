package com.campus.view_latest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.view_latest.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 获取所有用户的指定字段
     * @return 用户列表
     */
    @Select("SELECT User_ID, User_name, telephone, Create_at, User_sta, avatar_url FROM users")
    List<User> selectAllUsersWithSpecificFields();
}
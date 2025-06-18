package com.campus.ordermanagement.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.ordermanagement.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问层接口（订单管理模块专用）
 * 继承BaseMapper提供基本的CRUD操作
 * 只包含订单查询相关的方法
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {

    /**
     * 根据用户ID查找用户名
     * 
     * @param userId 用户ID
     * @return 用户名，如果不存在则返回null
     */
    @Select("SELECT User_name FROM users WHERE User_ID = #{userId}")
    String findUserNameById(@Param("userId") String userId);

    /**
     * 根据用户ID列表批量查询用户名
     * 
     * @param userIds 用户ID列表
     * @return 用户列表（只包含ID和用户名）
     */
    @Select("<script>" +
            "SELECT User_ID, User_name FROM users WHERE User_ID IN " +
            "<foreach collection='userIds' item='userId' open='(' separator=',' close=')'>" +
            "#{userId}" +
            "</foreach>" +
            "</script>")
    List<User> findUserNamesByIds(@Param("userIds") List<String> userIds);

    /**
     * 根据用户ID查找用户基本信息（用于订单显示）
     * 
     * @param userId 用户ID
     * @return 用户对象（只包含基本信息），如果不存在则返回null
     */
    @Select("SELECT User_ID, User_name, telephone, real_name, User_sta FROM users WHERE User_ID = #{userId}")
    User findBasicInfoById(@Param("userId") String userId);

    /**
     * 根据用户ID列表批量查询用户基本信息
     * 
     * @param userIds 用户ID列表
     * @return 用户列表（只包含基本信息）
     */
    @Select("<script>" +
            "SELECT User_ID, User_name, telephone, real_name, User_sta FROM users WHERE User_ID IN " +
            "<foreach collection='userIds' item='userId' open='(' separator=',' close=')'>" +
            "#{userId}" +
            "</foreach>" +
            "</script>")
    List<User> findBasicInfoByIds(@Param("userIds") List<String> userIds);

    /**
     * 检查用户是否存在
     * 
     * @param userId 用户ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE User_ID = #{userId}")
    boolean existsById(@Param("userId") String userId);

    /**
     * 检查用户是否被封号
     * 
     * @param userId 用户ID
     * @return 是否被封号
     */
    @Select("SELECT User_sta FROM users WHERE User_ID = #{userId}")
    Boolean isUserBanned(@Param("userId") String userId);
}
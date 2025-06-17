package com.campus.profile.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.profile.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户数据访问层接口
 * 继承BaseMapper提供基本的CRUD操作
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     * 
     * @param userName 用户名
     * @return 用户对象，如果不存在则返回null
     */
    @Select("SELECT * FROM users WHERE User_name = #{userName}")
    User findByUserName(@Param("userName") String userName);

    /**
     * 根据电话号码查找用户
     * 
     * @param telephone 电话号码
     * @return 用户对象，如果不存在则返回null
     */
    @Select("SELECT * FROM users WHERE telephone = #{telephone}")
    User findByTelephone(@Param("telephone") String telephone);

    /**
     * 根据身份证号查找用户
     * 
     * @param idCard 身份证号
     * @return 用户对象，如果不存在则返回null
     */
    @Select("SELECT * FROM users WHERE ID = #{idCard}")
    User findByIdCard(@Param("idCard") String idCard);

    /**
     * 检查用户名是否存在
     * 
     * @param userName 用户名
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE User_name = #{userName}")
    boolean existsByUserName(@Param("userName") String userName);

    /**
     * 检查电话号码是否存在
     * 
     * @param telephone 电话号码
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE telephone = #{telephone}")
    boolean existsByTelephone(@Param("telephone") String telephone);

    /**
     * 检查身份证号是否存在
     * 
     * @param idCard 身份证号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE ID = #{idCard}")
    boolean existsByIdCard(@Param("idCard") String idCard);

    /**
     * 根据用户状态查找用户列表
     * 
     * @param userStatus 用户状态（true-封号，false-正常）
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE User_sta = #{userStatus}")
    List<User> findByUserStatus(@Param("userStatus") Boolean userStatus);

    /**
     * 查找所有被封号的用户
     * 
     * @return 被封号的用户列表
     */
    @Select("SELECT * FROM users WHERE User_sta = true")
    List<User> findBannedUsers();

    /**
     * 查找所有正常状态的用户
     * 
     * @return 正常状态的用户列表
     */
    @Select("SELECT * FROM users WHERE User_sta = false")
    List<User> findActiveUsers();

    /**
     * 根据真实姓名模糊查询用户
     * 
     * @param realName 真实姓名关键字
     * @return 匹配的用户列表
     */
    @Select("SELECT * FROM users WHERE real_name LIKE CONCAT('%', #{realName}, '%')")
    List<User> findByRealNameContaining(@Param("realName") String realName);

    /**
     * 根据用户名模糊查询用户
     * 
     * @param userName 用户名关键字
     * @return 匹配的用户列表
     */
    @Select("SELECT * FROM users WHERE User_name LIKE CONCAT('%', #{userName}, '%')")
    List<User> findByUserNameContaining(@Param("userName") String userName);

    /**
     * 根据地理位置范围查找用户
     * 
     * @param minLongitude 最小经度
     * @param maxLongitude 最大经度
     * @param minLatitude 最小纬度
     * @param maxLatitude 最大纬度
     * @return 在指定范围内的用户列表
     */
    @Select("SELECT * FROM users WHERE User_loc_longitude BETWEEN #{minLongitude} AND #{maxLongitude} " +
           "AND User_loc_latitude BETWEEN #{minLatitude} AND #{maxLatitude}")
    List<User> findUsersInLocationRange(@Param("minLongitude") Double minLongitude,
                                       @Param("maxLongitude") Double maxLongitude,
                                       @Param("minLatitude") Double minLatitude,
                                       @Param("maxLatitude") Double maxLatitude);

    /**
     * 统计总用户数
     * 
     * @return 总用户数
     */
    @Select("SELECT COUNT(*) FROM users")
    long countTotalUsers();

    /**
     * 统计被封号用户数
     * 
     * @return 被封号用户数
     */
    @Select("SELECT COUNT(*) FROM users WHERE User_sta = true")
    long countBannedUsers();

    /**
     * 统计正常状态用户数
     * 
     * @return 正常状态用户数
     */
    @Select("SELECT COUNT(*) FROM users WHERE User_sta = false")
    long countActiveUsers();

    /**
     * 更新用户信息（用户名和电话号码）
     * 
     * @param userId 用户ID
     * @param userName 新的用户名
     * @param telephone 新的电话号码
     * @return 影响的行数
     */
    @Update("UPDATE users SET User_name = #{userName}, telephone = #{telephone} WHERE User_ID = #{userId}")
    int updateUserInfo(@Param("userId") String userId, 
                      @Param("userName") String userName, 
                      @Param("telephone") String telephone);

    /**
     * 更新用户头像URL
     * 
     * @param userId 用户ID
     * @param avatarUrl 新的头像URL
     * @return 影响的行数
     */
    @Update("UPDATE users SET avatar_url = #{avatarUrl} WHERE User_ID = #{userId}")
    int updateUserAvatar(@Param("userId") String userId, 
                        @Param("avatarUrl") String avatarUrl);
}
package com.campus.nearby.dao;

import com.campus.nearby.pojo.User;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserMapper {

    // 更新用户位置
    @Update("UPDATE users SET User_Loc_latitude = #{lat}, User_Loc_longitude = #{lon} WHERE User_ID = #{userId}")
    void updateUserLocation(@Param("userId") String userId,
                            @Param("lat") BigDecimal lat,
                            @Param("lon") BigDecimal lon);

    // 粗筛：包围框范围内的人
    @Select("SELECT * FROM users WHERE " +
            "User_Loc_latitude BETWEEN #{latMin} AND #{latMax} AND " +
            "User_Loc_longitude BETWEEN #{lonMin} AND #{lonMax} AND " +
            "User_ID != #{userId}")
    @Results({
            @Result(property = "userId", column = "User_ID"),
            @Result(property = "userName", column = "User_name"),
            @Result(property = "password", column = "password"),
            @Result(property = "telephone", column = "telephone"),
            @Result(property = "realName", column = "real_name"),
            @Result(property = "avatarUrl", column = "avatar_url"),
            @Result(property = "userLocLatitude", column = "User_Loc_latitude"),
            @Result(property = "userLocLongitude", column = "User_Loc_longitude"),
            @Result(property = "userSta", column = "User_sta"),
            @Result(property = "createAt", column = "Create_at"),
            @Result(property = "id", column = "ID")
    })
    List<User> findUsersInBoundingBox(@Param("latMin") double latMin,
                                      @Param("latMax") double latMax,
                                      @Param("lonMin") double lonMin,
                                      @Param("lonMax") double lonMax,
                                      @Param("userId") String userId);

}

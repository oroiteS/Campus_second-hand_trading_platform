package com.campus.ban.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("users")
public class User {
    
    @TableId(value = "User_ID", type = IdType.INPUT)
    private String userId;
    
    @TableField("User_name")
    private String userName;
    
    @TableField("password")
    private String password;
    
    @TableField("telephone")
    private String telephone;
    
    @TableField("real_name")
    private String realName;
    
    @TableField("avatar_url")
    private String avatarUrl;
    
    @TableField("User_Loc_longitude")
    private BigDecimal userLocLongitude;

    @TableField("User_sta")
    private Boolean userSta;
    
    @TableField("Create_at")
    private LocalDateTime createAt;
    
    @TableField("ID")
    private String idCard;
    
    @TableField("User_Loc_latitude")
    private BigDecimal userLocLatitude;
}
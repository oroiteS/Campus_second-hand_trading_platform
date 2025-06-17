package com.campus.view_latest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    
    @TableField("telephone")
    private String telephone;
    
    @TableField("Create_at")
    private LocalDateTime createAt;
    
    @TableField("User_sta")
    private Boolean userSta;
    
    @TableField("avatar_url")
    private String avatarUrl;
}
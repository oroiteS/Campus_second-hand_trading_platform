package com.campus.ordermanagement.pojo;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户实体类（订单管理模块专用）
 * 对应数据库中的users表
 * 只包含订单查询相关的字段
 */
@TableName("users")
public class User {

    /**
     * 用户ID - 主键
     */
    @TableId(value = "User_ID", type = IdType.INPUT)
    @NotBlank(message = "用户ID不能为空")
    @Size(max = 9, message = "用户ID长度不能超过9位")
    private String userId;

    /**
     * 用户名
     */
    @TableField("User_name")
    @NotBlank(message = "用户名不能为空")
    @Size(max = 20, message = "用户名长度不能超过20位")
    private String userName;

    /**
     * 电话号码
     */
    @TableField("telephone")
    @NotBlank(message = "电话号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "电话号码格式不正确")
    private String telephone;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50位")
    private String realName;

    /**
     * 用户状态 - 是否封号
     */
    @TableField("User_sta")
    @NotNull(message = "用户状态不能为空")
    private Boolean userStatus = false;

    /**
     * 创建时间
     */
    @TableField(value = "Create_at", fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    // 默认构造函数
    public User() {}

    // 带参构造函数
    public User(String userId, String userName, String telephone, String realName) {
        this.userId = userId;
        this.userName = userName;
        this.telephone = telephone;
        this.realName = realName;
        this.userStatus = false; // 默认未封号
    }

    // Getter和Setter方法
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    // toString方法
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", telephone='" + telephone + '\'' +
                ", realName='" + realName + '\'' +
                ", userStatus=" + userStatus +
                ", createAt=" + createAt +
                '}';
    }

    // equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    /**
     * 检查用户是否被封号
     */
    public boolean isBanned() {
        return userStatus != null && userStatus;
    }

    /**
     * 获取脱敏的电话号码
     */
    public String getMaskedTelephone() {
        if (telephone == null || telephone.length() != 11) {
            return telephone;
        }
        return telephone.substring(0, 3) + "****" + telephone.substring(7);
    }
}
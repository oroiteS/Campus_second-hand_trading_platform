package com.campus.profile.pojo;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户实体类
 * 对应数据库中的users表
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
     * 密码 - SHA256加密后的密码
     */
    @TableField("password")
    @NotBlank(message = "密码不能为空")
    @Size(max = 64, message = "密码长度不能超过64位")
    @JsonIgnore // 防止密码在JSON序列化时暴露
    private String password;

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
     * 头像URL
     */
    @TableField("avatar_url")
    @Size(max = 255, message = "头像URL长度不能超过255位")
    private String avatarUrl;

    /**
     * 用户位置经度
     */
    @TableField("User_Loc_longitude")
    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180.0", message = "经度值必须在-180到180之间")
    @DecimalMax(value = "180.0", message = "经度值必须在-180到180之间")
    private BigDecimal userLocLongitude;

    /**
     * 用户位置纬度
     */
    @TableField("User_Loc_latitude")
    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90.0", message = "纬度值必须在-90到90之间")
    @DecimalMax(value = "90.0", message = "纬度值必须在-90到90之间")
    private BigDecimal userLocLatitude;

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

    /**
     * 身份证号
     */
    @TableField("ID")
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", 
             message = "身份证号格式不正确")
    private String idCard;

    // 默认构造函数
    public User() {}

    // 带参构造函数
    public User(String userId, String userName, String password, String telephone, 
                String realName, BigDecimal userLocLongitude, BigDecimal userLocLatitude, 
                String idCard) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.telephone = telephone;
        this.realName = realName;
        this.userLocLongitude = userLocLongitude;
        this.userLocLatitude = userLocLatitude;
        this.idCard = idCard;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public BigDecimal getUserLocLongitude() {
        return userLocLongitude;
    }

    public void setUserLocLongitude(BigDecimal userLocLongitude) {
        this.userLocLongitude = userLocLongitude;
    }

    public BigDecimal getUserLocLatitude() {
        return userLocLatitude;
    }

    public void setUserLocLatitude(BigDecimal userLocLatitude) {
        this.userLocLatitude = userLocLatitude;
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

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    // toString方法（不包含敏感信息）
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", telephone='" + telephone + '\'' +
                ", realName='" + realName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", userLocLongitude=" + userLocLongitude +
                ", userLocLatitude=" + userLocLatitude +
                ", userStatus=" + userStatus +
                ", createAt=" + createAt +
                ", idCard='" + idCard + '\'' +
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

    // 业务方法
    
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

    /**
     * 获取脱敏的身份证号
     */
    public String getMaskedIdCard() {
        if (idCard == null || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }
}
package com.campus.nearby.pojo;

import java.math.BigDecimal;

public class User {
    private String userId;
    private String userName;
    private String password;
    private String telephone;
    private String realName;
    private String avatarUrl;
    private BigDecimal userLocLongitude;
    private BigDecimal userLocLatitude;
    private Boolean userSta;
    private String createAt;
    private String id;

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setUserLocLongitude(BigDecimal userLocLongitude) {
        this.userLocLongitude = userLocLongitude;
    }

    public void setUserLocLatitude(BigDecimal userLocLatitude) {
        this.userLocLatitude = userLocLatitude;
    }

    public Boolean getUserSta() {
        return userSta;
    }

    public void setUserSta(Boolean userSta) {
        this.userSta = userSta;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getUserLocLongitude() {
        return userLocLongitude;
    }

    public BigDecimal getUserLocLatitude() {
        return userLocLatitude;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}

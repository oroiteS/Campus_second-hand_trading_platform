package com.campus.cart.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Cart {
    private String cartId;
    private String userId;
    private String commodityId;
    private LocalDateTime createdAt;
    // Getters and Setters
    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCommodityId() { return commodityId; }
    public void setCommodityId(String commodityId) { this.commodityId = commodityId; }
}


package com.campus.wallet.pojo;

import jakarta.persistence.Id;

import java.math.BigDecimal;

public class Order {
    @Id
    private String order_id;
    private BigDecimal money;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}

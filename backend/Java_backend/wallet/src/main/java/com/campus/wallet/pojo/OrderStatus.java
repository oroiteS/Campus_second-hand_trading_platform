package com.campus.wallet.pojo;
public enum OrderStatus {
    pending_payment("pending_payment"),
    pending_transaction("pending_transaction"),
    completed("completed");

    @com.baomidou.mybatisplus.annotation.EnumValue
    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package com.campus.wallet.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
@TableName("orders")
public class Order {
    @TableField("order_id")
    @TableId(value = "order_id", type = IdType.INPUT)// 显式指定数据库字段名
    private String orderId;
    @TableField("Money")
    private BigDecimal money;
    @TableField("order_status")
    private OrderStatus orderStatus;

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrder_id() {
        return orderId;
    }

    public void setOrder_id(String order_id) {
        this.orderId = order_id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}

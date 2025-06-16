package com.campus.ordermanagement.pojo;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 订单实体类
 * 对应数据库中的orders表
 */
@TableName("orders")
public class Order {

    /**
     * 订单ID - 主键，使用UUIDv7
     */
    @TableId(value = "order_id", type = IdType.INPUT)
    @NotBlank(message = "订单ID不能为空")
    @Size(max = 36, message = "订单ID长度不能超过36位")
    private String orderId;

    /**
     * 商品ID - 外键，与商品表关联
     */
    @TableField("commodity_id")
    @NotBlank(message = "商品ID不能为空")
    @Size(max = 36, message = "商品ID长度不能超过36位")
    private String commodityId;

    /**
     * 买家ID
     */
    @TableField("Buyer_id")
    @NotBlank(message = "买家ID不能为空")
    @Size(max = 9, message = "买家ID长度不能超过9位")
    private String buyerId;

    /**
     * 卖家ID
     */
    @TableField("Seller_id")
    @NotBlank(message = "卖家ID不能为空")
    @Size(max = 9, message = "卖家ID长度不能超过9位")
    private String sellerId;

    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        PENDING_PAYMENT("pending_payment", "待付款"),
        PENDING_TRANSACTION("pending_transaction", "待交易"),
        COMPLETED("completed", "已完成");

        private final String code;
        private final String description;

        OrderStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static OrderStatus fromCode(String code) {
            for (OrderStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("未知的订单状态: " + code);
        }
    }

    /**
     * 订单状态
     */
    @TableField("order_status")
    @NotNull(message = "订单状态不能为空")
    private OrderStatus orderStatus = OrderStatus.PENDING_PAYMENT;

    /**
     * 交易时间
     */
    @TableField("Sale_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime saleTime;

    /**
     * 交易金额
     */
    @TableField("Money")
    @DecimalMin(value = "0.01", message = "交易金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "交易金额格式不正确")
    private BigDecimal money;

    /**
     * 交易地址
     */
    @TableField("Sale_loc")
    @Size(max = 250, message = "交易地址长度不能超过250位")
    private String saleLocation;

    // 默认构造函数
    public Order() {}

    // 带参构造函数
    public Order(String orderId, String commodityId, String buyerId, String sellerId, 
                 OrderStatus orderStatus, BigDecimal money, String saleLocation) {
        this.orderId = orderId;
        this.commodityId = commodityId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.orderStatus = orderStatus != null ? orderStatus : OrderStatus.PENDING_PAYMENT;
        this.money = money;
        this.saleLocation = saleLocation;
    }

    // Getter和Setter方法
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(LocalDateTime saleTime) {
        this.saleTime = saleTime;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getSaleLocation() {
        return saleLocation;
    }

    public void setSaleLocation(String saleLocation) {
        this.saleLocation = saleLocation;
    }

    // toString方法
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", commodityId='" + commodityId + '\'' +
                ", buyerId='" + buyerId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", orderStatus=" + orderStatus +
                ", saleTime=" + saleTime +
                ", money=" + money +
                ", saleLocation='" + saleLocation + '\'' +
                '}';
    }

    // equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    // 业务方法
    
    /**
     * 检查订单是否已完成
     */
    public boolean isCompleted() {
        return OrderStatus.COMPLETED.equals(this.orderStatus);
    }

    /**
     * 检查订单是否待付款
     */
    public boolean isPendingPayment() {
        return OrderStatus.PENDING_PAYMENT.equals(this.orderStatus);
    }

    /**
     * 检查订单是否待交易
     */
    public boolean isPendingTransaction() {
        return OrderStatus.PENDING_TRANSACTION.equals(this.orderStatus);
    }

    /**
     * 完成订单
     */
    public void completeOrder() {
        this.orderStatus = OrderStatus.COMPLETED;
        this.saleTime = LocalDateTime.now();
    }

    /**
     * 确认付款，将状态从待付款改为待交易
     */
    public void confirmPayment() {
        if (isPendingPayment()) {
            this.orderStatus = OrderStatus.PENDING_TRANSACTION;
        } else {
            throw new IllegalStateException("只有待付款状态的订单才能确认付款");
        }
    }

    /**
     * 获取订单状态的中文描述
     */
    public String getOrderStatusDescription() {
        return orderStatus != null ? orderStatus.getDescription() : "未知状态";
    }
}
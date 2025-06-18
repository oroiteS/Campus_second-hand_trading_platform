package com.campus.ordermanagement.dto;

import com.campus.ordermanagement.pojo.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应DTO
 */
public class OrderResponse {

    /**
     * 订单ID
     */
    @JsonProperty("orderId")
    private String orderId;

    /**
     * 商品ID
     */
    @JsonProperty("commodityId")
    private String commodityId;

    /**
     * 买家ID
     */
    @JsonProperty("buyerId")
    private String buyerId;

    /**
     * 卖家ID
     */
    @JsonProperty("sellerId")
    private String sellerId;

    /**
     * 订单状态
     */
    @JsonProperty("orderStatus")
    private String orderStatus;

    /**
     * 订单状态描述
     */
    @JsonProperty("orderStatusDescription")
    private String orderStatusDescription;

    /**
     * 交易时间
     */
    @JsonProperty("saleTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime saleTime;

    /**
     * 交易金额
     */
    @JsonProperty("money")
    private BigDecimal money;

    /**
     * 交易地址
     */
    @JsonProperty("saleLocation")
    private String saleLocation;

    /**
     * 创建时间（可选，用于排序）
     */
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 默认构造函数
    public OrderResponse() {}

    // 从Order实体转换的构造函数
    public OrderResponse(Order order) {
        this.orderId = order.getOrderId();
        this.commodityId = order.getCommodityId();
        this.buyerId = order.getBuyerId();
        this.sellerId = order.getSellerId();
        this.orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().getCode() : null;
        this.orderStatusDescription = order.getOrderStatusDescription();
        this.saleTime = order.getSaleTime();
        this.money = order.getMoney();
        this.saleLocation = order.getSaleLocation();
        this.createTime = LocalDateTime.now(); // 可以根据实际需求调整
    }

    // 带参构造函数
    public OrderResponse(String orderId, String commodityId, String buyerId, String sellerId,
                        String orderStatus, String orderStatusDescription, LocalDateTime saleTime,
                        BigDecimal money, String saleLocation) {
        this.orderId = orderId;
        this.commodityId = commodityId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.orderStatus = orderStatus;
        this.orderStatusDescription = orderStatusDescription;
        this.saleTime = saleTime;
        this.money = money;
        this.saleLocation = saleLocation;
        this.createTime = LocalDateTime.now();
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatusDescription() {
        return orderStatusDescription;
    }

    public void setOrderStatusDescription(String orderStatusDescription) {
        this.orderStatusDescription = orderStatusDescription;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "orderId='" + orderId + '\'' +
                ", commodityId='" + commodityId + '\'' +
                ", buyerId='" + buyerId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderStatusDescription='" + orderStatusDescription + '\'' +
                ", saleTime=" + saleTime +
                ", money=" + money +
                ", saleLocation='" + saleLocation + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
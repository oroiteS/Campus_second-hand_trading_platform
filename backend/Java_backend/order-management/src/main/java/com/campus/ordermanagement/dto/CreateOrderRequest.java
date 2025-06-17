package com.campus.ordermanagement.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 创建订单请求DTO
 */
public class CreateOrderRequest {

    /**
     * 商品ID
     */
    @NotBlank(message = "商品ID不能为空")
    @Size(max = 36, message = "商品ID长度不能超过36位")
    @JsonProperty("commodityId")
    private String commodityId;

    /**
     * 买家ID
     */
    @NotBlank(message = "买家ID不能为空")
    @Size(max = 9, message = "买家ID长度不能超过9位")
    @JsonProperty("buyerId")
    private String buyerId;

    /**
     * 卖家ID
     */
    @NotBlank(message = "卖家ID不能为空")
    @Size(max = 9, message = "卖家ID长度不能超过9位")
    @JsonProperty("sellerId")
    private String sellerId;

    /**
     * 交易金额
     */
    @NotNull(message = "交易金额不能为空")
    @DecimalMin(value = "0.01", message = "交易金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "交易金额格式不正确")
    @JsonProperty("money")
    private BigDecimal money;

    /**
     * 交易地址
     */
    @Size(max = 250, message = "交易地址长度不能超过250位")
    @JsonProperty("saleLocation")
    private String saleLocation;

    // 默认构造函数
    public CreateOrderRequest() {}

    // 带参构造函数
    public CreateOrderRequest(String commodityId, String buyerId, String sellerId, 
                            BigDecimal money, String saleLocation) {
        this.commodityId = commodityId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.money = money;
        this.saleLocation = saleLocation;
    }

    // Getter和Setter方法
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

    @Override
    public String toString() {
        return "CreateOrderRequest{" +
                "commodityId='" + commodityId + '\'' +
                ", buyerId='" + buyerId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", money=" + money +
                ", saleLocation='" + saleLocation + '\'' +
                '}';
    }
}
package com.campus.productquery.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体类
 * 对应commodities表
 */
@TableName("commodities")
public class Commodity {

    /**
     * 商品唯一标识符（UUIDv7）
     */
    @TableId("commodity_id")
    @JsonProperty("commodityId")
    private String commodityId;

    /**
     * 商品标题
     */
    @TableField("commodity_name")
    @JsonProperty("commodityName")
    private String commodityName;

    /**
     * 详细描述
     */
    @TableField("commodity_description")
    @JsonProperty("commodityDescription")
    private String commodityDescription;

    /**
     * 关联类别表的外键
     */
    @TableField("category_id")
    @JsonProperty("categoryId")
    private Integer categoryId;

    /**
     * 存储标签ID数组
     */
    @TableField("tags_Id")
    @JsonProperty("tagsId")
    private String tagsId; // JSON字符串形式存储

    /**
     * 商品售价
     */
    @TableField("current_price")
    @JsonProperty("currentPrice")
    private BigDecimal currentPrice;

    /**
     * 商品状态：在售/已售/下架
     */
    @TableField("commodity_status")
    @JsonProperty("commodityStatus")
    private String commodityStatus;

    /**
     * 关联用户表的外键
     */
    @TableField("seller_id")
    @JsonProperty("sellerId")
    private String sellerId;

    /**
     * 商品主图链接
     */
    @TableField("main_image_url")
    @JsonProperty("mainImageUrl")
    private String mainImageUrl;

    /**
     * 多图链接数组
     */
    @TableField("image_list")
    @JsonProperty("imageList")
    private String imageList; // JSON字符串形式存储

    /**
     * 商品发布时间
     */
    @TableField("created_at")
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 信息更新时间
     */
    @TableField("updated_at")
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 商品数量
     */
    @TableField("quantity")
    @JsonProperty("quantity")
    private Integer quantity;

    /**
     * 商品新旧度
     */
    @TableField("newness")
    @JsonProperty("newness")
    private String newness;

    // 常量定义
    public static final String STATUS_ON_SALE = "on_sale";
    public static final String STATUS_SOLD = "sold";
    public static final String STATUS_OFF_SALE = "off_sale";

    // 构造函数
    public Commodity() {}

    public Commodity(String commodityId, String commodityName, String commodityDescription,
                    Integer categoryId, String tagsId, BigDecimal currentPrice,
                    String commodityStatus, String sellerId, String mainImageUrl,
                    String imageList, Integer quantity, String newness) {
        this.commodityId = commodityId;
        this.commodityName = commodityName;
        this.commodityDescription = commodityDescription;
        this.categoryId = categoryId;
        this.tagsId = tagsId;
        this.currentPrice = currentPrice;
        this.commodityStatus = commodityStatus;
        this.sellerId = sellerId;
        this.mainImageUrl = mainImageUrl;
        this.imageList = imageList;
        this.quantity = quantity;
        this.newness = newness;
    }

    // Getter和Setter方法
    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityDescription() {
        return commodityDescription;
    }

    public void setCommodityDescription(String commodityDescription) {
        this.commodityDescription = commodityDescription;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getTagsId() {
        return tagsId;
    }

    public void setTagsId(String tagsId) {
        this.tagsId = tagsId;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getCommodityStatus() {
        return commodityStatus;
    }

    public void setCommodityStatus(String commodityStatus) {
        this.commodityStatus = commodityStatus;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getImageList() {
        return imageList;
    }

    public void setImageList(String imageList) {
        this.imageList = imageList;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNewness() {
        return newness;
    }

    public void setNewness(String newness) {
        this.newness = newness;
    }

    @Override
    public String toString() {
        return "Commodity{" +
                "commodityId='" + commodityId + '\'' +
                ", commodityName='" + commodityName + '\'' +
                ", commodityDescription='" + commodityDescription + '\'' +
                ", categoryId=" + categoryId +
                ", tagsId='" + tagsId + '\'' +
                ", currentPrice=" + currentPrice +
                ", commodityStatus='" + commodityStatus + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", mainImageUrl='" + mainImageUrl + '\'' +
                ", imageList='" + imageList + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", quantity=" + quantity +
                ", newness='" + newness + '\'' +
                '}';
    }
}
package com.campus.productquery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品查询请求DTO
 * 支持多种查询条件的组合
 */
public class CommodityQueryRequest {

    /**
     * 商品ID
     */
    @JsonProperty("commodityId")
    @Schema(description = "商品ID", example = "COMM001")
    private String commodityId;

    /**
     * 商品名称（支持模糊查询）
     */
    @JsonProperty("commodityName")
    @Schema(description = "商品名称，支持模糊查询", example = "iPhone")
    private String commodityName;

    /**
     * 类别ID
     */
    @JsonProperty("categoryId")
    @Schema(description = "商品类别ID", example = "1")
    private Integer categoryId;

    /**
     * 商品状态
     */
    @JsonProperty("commodityStatus")
    @Schema(description = "商品状态", example = "available", allowableValues = {"available", "sold", "reserved"})
    private String commodityStatus;

    /**
     * 卖家ID
     */
    @JsonProperty("sellerId")
    @Schema(description = "卖家ID", example = "USER001")
    private String sellerId;

    /**
     * 最小价格
     */
    @JsonProperty("minPrice")
    @DecimalMin(value = "0.00", message = "最小价格不能小于0")
    @Schema(description = "最小价格", example = "100.00")
    private BigDecimal minPrice;

    /**
     * 最大价格
     */
    @JsonProperty("maxPrice")
    @DecimalMin(value = "0.01", message = "最大价格必须大于0")
    @Schema(description = "最大价格", example = "1000.00")
    private BigDecimal maxPrice;

    /**
     * 标签ID列表
     */
    @JsonProperty("tagIds")
    @Schema(description = "标签ID列表", example = "[1, 2, 3]")
    private List<Integer> tagIds;

    /**
     * 商品新旧度
     */
    @JsonProperty("newness")
    @Schema(description = "商品新旧度", example = "全新", allowableValues = {"全新", "几乎全新", "轻微使用痕迹", "明显使用痕迹"})
    private String newness;

    /**
     * 价格区间（0-100, 100-500, 500-1000, 1000+）
     */
    @JsonProperty("priceRange")
    @Schema(description = "价格区间筛选",
            example = "50-200",
            allowableValues = {"0-50", "50-200", "200-500", "500-1000", "1000+"})
    private String priceRange;

    /**
     * 最小数量
     */
    @JsonProperty("minQuantity")
    @Min(value = 0, message = "最小数量不能小于0")
    @Schema(description = "最小库存数量", example = "1")
    private Integer minQuantity;

    /**
     * 排序字段
     */
    @JsonProperty("sortBy")
    @Schema(description = "排序字段", example = "created_at", allowableValues = {"created_at", "current_price", "commodity_name"})
    private String sortBy;

    /**
     * 排序方向（ASC/DESC）
     */
    @JsonProperty("sortDirection")
    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortDirection;

    /**
     * 页码（从1开始）
     */
    @JsonProperty("pageNum")
    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @JsonProperty("pageSize")
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    @Schema(description = "每页显示数量，最大100", example = "10")
    private Integer pageSize;

    // 构造函数
    public CommodityQueryRequest() {
        this.pageNum = 1;
        this.pageSize = 10;
        this.sortBy = "created_at";
        this.sortDirection = "DESC";
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<Integer> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Integer> tagIds) {
        this.tagIds = tagIds;
    }

    public String getNewness() {
        return newness;
    }

    public void setNewness(String newness) {
        this.newness = newness;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public Integer getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "CommodityQueryRequest{" +
                "commodityId='" + commodityId + '\'' +
                ", commodityName='" + commodityName + '\'' +
                ", categoryId=" + categoryId +
                ", commodityStatus='" + commodityStatus + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", tagIds=" + tagIds +
                ", newness='" + newness + '\'' +
                ", priceRange='" + priceRange + '\'' +
                ", minQuantity=" + minQuantity +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
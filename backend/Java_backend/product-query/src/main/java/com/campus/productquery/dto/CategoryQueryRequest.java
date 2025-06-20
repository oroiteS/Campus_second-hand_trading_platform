package com.campus.productquery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * 分类查询请求DTO
 * 专门用于前端分类查找和筛选功能
 */
public class CategoryQueryRequest {

    /**
     * 分类ID（1-8）
     */
    @JsonProperty("categoryId")
    @NotNull(message = "分类ID不能为空")
    @Min(value = 1, message = "分类ID必须在1-8之间")
    @Max(value = 8, message = "分类ID必须在1-8之间")
    @Schema(description = "分类ID，支持1-8", example = "1", minimum = "1", maximum = "8")
    private Integer categoryId;

    /**
     * 价格区间筛选
     */
    @JsonProperty("priceRange")
    @Schema(description = "价格区间筛选",
            example = "50-200",
            allowableValues = {"0-50", "50-200", "200-500", "500-1000", "1000+"})
    private String priceRange;

    /**
     * 商品新旧度筛选
     */
    @JsonProperty("newness")
    @Schema(description = "商品新旧度筛选", 
            example = "全新",
            allowableValues = {"全新", "95新", "9新"})
    private String newness;

    /**
     * 排序方式
     */
    @JsonProperty("sortBy")
    @Schema(description = "排序方式", 
            example = "time_desc",
            allowableValues = {"price_asc", "price_desc", "time_desc", "time_asc"})
    private String sortBy;

    /**
     * 页码（从1开始）
     */
    @JsonProperty("pageNum")
    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "页码，从1开始", example = "1", minimum = "1")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @JsonProperty("pageSize")
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    @Schema(description = "每页显示数量，最大100", example = "10", minimum = "1", maximum = "100")
    private Integer pageSize;

    // 构造函数
    public CategoryQueryRequest() {
        this.pageNum = 1;
        this.pageSize = 10;
        this.sortBy = "time_desc"; // 默认按发布时间从新到旧排序
    }

    // Getter和Setter方法
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getNewness() {
        return newness;
    }

    public void setNewness(String newness) {
        this.newness = newness;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
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
        return "CategoryQueryRequest{" +
                "categoryId=" + categoryId +
                ", priceRange='" + priceRange + '\'' +
                ", newness='" + newness + '\'' +
                ", sortBy='" + sortBy + '\'' +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
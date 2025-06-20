package com.campus.productquery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 分页商品响应DTO
 */
public class PagedCommodityResponse {

    /**
     * 商品列表
     */
    @JsonProperty("commodities")
    private List<CommodityResponse> commodities;

    /**
     * 当前页码
     */
    @JsonProperty("currentPage")
    private Integer currentPage;

    /**
     * 每页大小
     */
    @JsonProperty("pageSize")
    private Integer pageSize;

    /**
     * 总记录数
     */
    @JsonProperty("totalCount")
    private Long totalCount;

    /**
     * 总页数
     */
    @JsonProperty("totalPages")
    private Integer totalPages;

    /**
     * 是否有下一页
     */
    @JsonProperty("hasNext")
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    @JsonProperty("hasPrevious")
    private Boolean hasPrevious;

    // 构造函数
    public PagedCommodityResponse() {}

    public PagedCommodityResponse(List<CommodityResponse> commodities, Integer currentPage, 
                                 Integer pageSize, Long totalCount) {
        this.commodities = commodities;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
        this.hasNext = currentPage < totalPages;
        this.hasPrevious = currentPage > 1;
    }

    // Getter和Setter方法
    public List<CommodityResponse> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<CommodityResponse> commodities) {
        this.commodities = commodities;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Boolean getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    @Override
    public String toString() {
        return "PagedCommodityResponse{" +
                "commodities=" + commodities +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", totalPages=" + totalPages +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                '}';
    }
}
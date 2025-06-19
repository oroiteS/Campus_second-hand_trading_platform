package com.campus.ordermanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 分页查询订单响应DTO
 */
@Schema(description = "分页查询订单响应")
public class PagedOrderResponse {

    /**
     * 订单列表
     */
    @Schema(description = "订单列表")
    @JsonProperty("orders")
    private List<OrderResponse> orders;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    @JsonProperty("pageNum")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    @JsonProperty("pageSize")
    private Integer pageSize;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    @JsonProperty("total")
    private Long total;

    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "10")
    @JsonProperty("totalPages")
    private Integer totalPages;

    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页", example = "true")
    @JsonProperty("hasNext")
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    @Schema(description = "是否有上一页", example = "false")
    @JsonProperty("hasPrevious")
    private Boolean hasPrevious;

    // 默认构造函数
    public PagedOrderResponse() {}

    // 带参构造函数
    public PagedOrderResponse(List<OrderResponse> orders, Integer pageNum, Integer pageSize, Long total) {
        this.orders = orders;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.hasNext = pageNum < totalPages;
        this.hasPrevious = pageNum > 1;
    }

    // Getter and Setter
    public List<OrderResponse> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponse> orders) {
        this.orders = orders;
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

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
        if (pageSize != null && pageSize > 0) {
            this.totalPages = (int) Math.ceil((double) total / pageSize);
            if (pageNum != null) {
                this.hasNext = pageNum < totalPages;
                this.hasPrevious = pageNum > 1;
            }
        }
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
        return "PagedOrderResponse{" +
                "orders=" + orders +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", totalPages=" + totalPages +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                '}';
    }
}
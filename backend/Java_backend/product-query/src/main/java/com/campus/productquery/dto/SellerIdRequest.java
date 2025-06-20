package com.campus.productquery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 卖家ID查询请求DTO
 */
public class SellerIdRequest {

    /**
     * 卖家ID
     */
    @JsonProperty("sellerId")
    @Schema(description = "卖家ID", example = "USER001", required = true)
    @NotBlank(message = "卖家ID不能为空")
    private String sellerId;

    public SellerIdRequest() {}

    public SellerIdRequest(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String toString() {
        return "SellerIdRequest{" +
                "sellerId='" + sellerId + '\'' +
                '}';
    }
}
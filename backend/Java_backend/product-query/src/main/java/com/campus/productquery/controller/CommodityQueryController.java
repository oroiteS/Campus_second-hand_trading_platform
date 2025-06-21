package com.campus.productquery.controller;

import com.campus.productquery.dto.*;
import com.campus.productquery.service.CommodityQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 商品分类查询API控制器
 * 专门处理前端分类查找和筛选功能
 */
@RestController
@RequestMapping("/api-8096/v1/commodities")
@Tag(name = "商品分类查询API", description = "提供商品分类查询和筛选功能的API接口")
@Validated
public class CommodityQueryController {

    @Autowired
    private CommodityQueryService commodityQueryService;

    /**
     * 按分类查询商品（支持筛选和排序）
     * 前端传入category1到8，支持价格区间、新旧度筛选和排序
     */
    @PostMapping("/category")
    @Operation(
            summary = "按分类查询商品", 
            description = "根据分类ID查询商品，支持价格区间、新旧度筛选和排序功能。" +
                         "\n\n**支持的分类ID：** 1-8" +
                         "\n\n**价格区间选项：**" +
            "\n- `0-50`: 0到50元" +
            "\n- `50-200`: 50到200元" +
            "\n- `200-500`: 200到500元" +
            "\n- `500-1000`: 500到1000元" +
            "\n- `1000+`: 1000元以上" +
                         "\n\n**新旧度选项：**" +
                         "\n- `全新`: 全新商品" +
                         "\n- `95新`: 95新商品" +
                         "\n- `9新`: 9新商品" +
                         "\n\n**排序选项：**" +
                         "\n- `price_asc`: 价格从低到高" +
                         "\n- `price_desc`: 价格从高到低" +
                         "\n- `time_desc`: 发布时间从新到旧（默认）" +
                         "\n- `time_asc`: 发布时间从旧到新"
    )
    public ResponseEntity<Map<String, Object>> getCommoditiesByCategory(
            @Parameter(
                    description = "分类查询请求", 
                    required = true,
                    example = "{\"categoryId\": 1, \"priceRange\": \"50-200\", \"newness\": \"全新\", \"sortBy\": \"price_asc\", \"pageNum\": 1, \"pageSize\": 10}"
            )
            @RequestBody @Valid CategoryQueryRequest request) {
        try {
            // 验证分类ID范围
            if (request.getCategoryId() < 1 || request.getCategoryId() > 8) {
                return createErrorResponse("分类ID必须在1-8之间", HttpStatus.BAD_REQUEST);
            }
            
            PagedCommodityResponse result = commodityQueryService.getCommoditiesByCategory(request);
            return createSuccessResponse("查询成功", result);
        } catch (IllegalArgumentException e) {
            return createErrorResponse("参数错误: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("查询失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * 创建成功响应
     */
    private ResponseEntity<Map<String, Object>> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * 创建错误响应
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("data", null);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        return createErrorResponse("服务器内部错误: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
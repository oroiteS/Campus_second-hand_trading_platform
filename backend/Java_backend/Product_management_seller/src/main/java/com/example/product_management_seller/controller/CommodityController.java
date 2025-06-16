package com.example.product_management_seller.controller;

import com.example.product_management_seller.dto.ApiResponse;
import com.example.product_management_seller.dto.CommodityDescriptionUpdateRequest;
import com.example.product_management_seller.dto.CommodityStatusUpdateRequest;
import com.example.product_management_seller.entity.Commodity;
import com.example.product_management_seller.service.CommodityService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/commodity")
@CrossOrigin(originPatterns = "*", maxAge = 3600, allowCredentials = "true")
public class CommodityController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommodityController.class);
    
    @Autowired
    private CommodityService commodityService;
    
    /**
     * 上架商品
     * @param request 上架请求
     * @param bindingResult 验证结果
     * @return 响应结果
     */
    @PostMapping("/put-on-sale")
    public ResponseEntity<ApiResponse<Void>> putOnSale(
            @Valid @RequestBody CommodityStatusUpdateRequest request,
            BindingResult bindingResult) {
        
        logger.info("收到上架商品请求: {}", request);
        
        // 参数验证
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            logger.warn("上架商品参数验证失败: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("参数验证失败: " + errorMessage, "VALIDATION_ERROR"));
        }
        
        // 验证状态参数
        if (!"on_sale".equals(request.getStatus())) {
            logger.warn("上架商品状态参数错误: {}", request.getStatus());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("状态参数错误，上架操作状态应为on_sale", "INVALID_STATUS"));
        }
        
        try {
            boolean success = commodityService.putOnSale(request.getCommodityId(), request.getSellerId());
            
            if (success) {
                logger.info("商品上架成功: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return ResponseEntity.ok(ApiResponse.success("商品上架成功"));
            } else {
                logger.warn("商品上架失败，商品不存在或无权限: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("商品不存在或您无权限操作此商品", "COMMODITY_NOT_FOUND"));
            }
        } catch (Exception e) {
            logger.error("商品上架异常: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("服务器内部错误: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 下架商品
     * @param request 下架请求
     * @param bindingResult 验证结果
     * @return 响应结果
     */
    @PostMapping("/put-off-sale")
    public ResponseEntity<ApiResponse<Void>> putOffSale(
            @Valid @RequestBody CommodityStatusUpdateRequest request,
            BindingResult bindingResult) {
        
        logger.info("收到下架商品请求: {}", request);
        
        // 参数验证
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            logger.warn("下架商品参数验证失败: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("参数验证失败: " + errorMessage, "VALIDATION_ERROR"));
        }
        
        // 验证状态参数
        if (!"off_sale".equals(request.getStatus())) {
            logger.warn("下架商品状态参数错误: {}", request.getStatus());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("状态参数错误，下架操作状态应为off_sale", "INVALID_STATUS"));
        }
        
        try {
            boolean success = commodityService.putOffSale(request.getCommodityId(), request.getSellerId());
            
            if (success) {
                logger.info("商品下架成功: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return ResponseEntity.ok(ApiResponse.success("商品下架成功"));
            } else {
                logger.warn("商品下架失败，商品不存在或无权限: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("商品不存在或您无权限操作此商品", "COMMODITY_NOT_FOUND"));
            }
        } catch (Exception e) {
            logger.error("商品下架异常: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("服务器内部错误: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 修改商品描述
     * @param request 修改请求
     * @param bindingResult 验证结果
     * @return 响应结果
     */
    @PostMapping("/update-description")
    public ResponseEntity<ApiResponse<Void>> updateDescription(
            @Valid @RequestBody CommodityDescriptionUpdateRequest request,
            BindingResult bindingResult) {
        
        logger.info("收到修改商品描述请求: {}", request);
        
        // 参数验证
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            logger.warn("修改商品描述参数验证失败: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("参数验证失败: " + errorMessage, "VALIDATION_ERROR"));
        }
        
        try {
            boolean success = commodityService.updateDescription(
                    request.getCommodityId(), 
                    request.getSellerId(), 
                    request.getDescription());
            
            if (success) {
                logger.info("商品描述修改成功: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return ResponseEntity.ok(ApiResponse.success("商品描述修改成功"));
            } else {
                logger.warn("商品描述修改失败，商品不存在或无权限: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("商品不存在或您无权限操作此商品", "COMMODITY_NOT_FOUND"));
            }
        } catch (Exception e) {
            logger.error("商品描述修改异常: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("服务器内部错误: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 获取卖家的所有商品
     * @param sellerId 卖家ID
     * @return 商品列表
     */
    @GetMapping("/list/{sellerId}")
    public ResponseEntity<ApiResponse<List<Commodity>>> getCommoditiesBySeller(
            @PathVariable String sellerId) {
        
        logger.info("收到获取卖家商品列表请求: sellerId={}", sellerId);
        
        if (sellerId == null || sellerId.trim().isEmpty()) {
            logger.warn("卖家ID为空");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("卖家ID不能为空", "INVALID_SELLER_ID"));
        }
        
        try {
            List<Commodity> commodities = commodityService.getCommoditiesBySeller(sellerId);
            logger.info("获取卖家商品列表成功: sellerId={}, count={}", sellerId, commodities.size());
            return ResponseEntity.ok(ApiResponse.success("获取商品列表成功", commodities));
        } catch (Exception e) {
            logger.error("获取卖家商品列表异常: sellerId={}, error={}", sellerId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("服务器内部错误: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 根据状态获取卖家的商品
     * @param sellerId 卖家ID
     * @param status 商品状态
     * @return 商品列表
     */
    @GetMapping("/list/{sellerId}/status/{status}")
    public ResponseEntity<ApiResponse<List<Commodity>>> getCommoditiesBySellerAndStatus(
            @PathVariable String sellerId,
            @PathVariable String status) {
        
        logger.info("收到根据状态获取卖家商品列表请求: sellerId={}, status={}", sellerId, status);
        
        if (sellerId == null || sellerId.trim().isEmpty()) {
            logger.warn("卖家ID为空");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("卖家ID不能为空", "INVALID_SELLER_ID"));
        }
        
        // 验证状态参数
        Commodity.CommodityStatus commodityStatus;
        try {
            switch (status.toLowerCase()) {
                case "on_sale":
                    commodityStatus = Commodity.CommodityStatus.ON_SALE;
                    break;
                case "off_sale":
                    commodityStatus = Commodity.CommodityStatus.OFF_SALE;
                    break;
                case "sold":
                    commodityStatus = Commodity.CommodityStatus.SOLD;
                    break;
                default:
                    logger.warn("无效的商品状态: {}", status);
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("无效的商品状态，支持的状态: on_sale, off_sale, sold", "INVALID_STATUS"));
            }
        } catch (Exception e) {
            logger.warn("商品状态解析失败: {}", status);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("商品状态格式错误", "INVALID_STATUS_FORMAT"));
        }
        
        try {
            List<Commodity> commodities = commodityService.getCommoditiesBySellerAndStatus(sellerId, commodityStatus);
            logger.info("根据状态获取卖家商品列表成功: sellerId={}, status={}, count={}", 
                       sellerId, status, commodities.size());
            return ResponseEntity.ok(ApiResponse.success("获取商品列表成功", commodities));
        } catch (Exception e) {
            logger.error("根据状态获取卖家商品列表异常: sellerId={}, status={}, error={}", 
                        sellerId, status, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("服务器内部错误: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 获取商品详情
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 商品详情
     */
    @GetMapping("/detail/{commodityId}/seller/{sellerId}")
    public ResponseEntity<ApiResponse<Commodity>> getCommodityDetail(
            @PathVariable String commodityId,
            @PathVariable String sellerId) {
        
        logger.info("收到获取商品详情请求: commodityId={}, sellerId={}", commodityId, sellerId);
        
        if (commodityId == null || commodityId.trim().isEmpty()) {
            logger.warn("商品ID为空");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("商品ID不能为空", "INVALID_COMMODITY_ID"));
        }
        
        if (sellerId == null || sellerId.trim().isEmpty()) {
            logger.warn("卖家ID为空");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("卖家ID不能为空", "INVALID_SELLER_ID"));
        }
        
        try {
            Optional<Commodity> commodity = commodityService.getCommodityDetail(commodityId, sellerId);
            
            if (commodity.isPresent()) {
                logger.info("获取商品详情成功: commodityId={}, sellerId={}", commodityId, sellerId);
                return ResponseEntity.ok(ApiResponse.success("获取商品详情成功", commodity.get()));
            } else {
                logger.warn("商品不存在或无权限: commodityId={}, sellerId={}", commodityId, sellerId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("商品不存在或您无权限查看此商品", "COMMODITY_NOT_FOUND"));
            }
        } catch (Exception e) {
            logger.error("获取商品详情异常: commodityId={}, sellerId={}, error={}", 
                        commodityId, sellerId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("服务器内部错误: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 健康检查接口
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        logger.debug("收到健康检查请求");
        return ResponseEntity.ok(ApiResponse.success("商品管理服务运行正常", "OK"));
    }
}
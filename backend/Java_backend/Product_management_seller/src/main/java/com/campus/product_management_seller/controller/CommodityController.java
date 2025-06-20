package com.campus.product_management_seller.controller;

import com.campus.product_management_seller.dto.ApiResponse;
import com.campus.product_management_seller.dto.CommodityDescriptionUpdateRequest;
import com.campus.product_management_seller.dto.CommodityStatusUpdateRequest;
import com.campus.product_management_seller.dto.CommodityCreateRequest;
import com.campus.product_management_seller.dto.CommodityUpdateRequest;
import com.campus.product_management_seller.service.CommodityService;
import com.campus.product_management_seller.service.TagService;
import com.campus.product_management_seller.entity.Commodity;
import com.campus.product_management_seller.dto.TagDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/commodity")
@CrossOrigin(originPatterns = "*", maxAge = 3600, allowCredentials = "true")
@Tag(name = "商品管理", description = "商品管理相关接口")
public class CommodityController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommodityController.class);
    
    @Autowired
    private CommodityService commodityService;
    
    @Autowired
    private TagService tagService;
    
    /**
     * 创建商品接口（待审核状态）
     * @param commodityName 商品名称
     * @param commodityDescription 商品描述
     * @param categoryId 分类ID
     * @param tagsId 标签ID
     * @param currentPrice 当前价格
     * @param quantity 数量
     * @param sellerId 卖家ID
     * @param newness 新旧度
     * @param images 商品图片
     * @return 响应结果
     */
    @Operation(summary = "创建商品", description = "创建新商品，状态为待审核(to_sale)，需要管理员审核后才能正式上架")
    @PostMapping(value = "/create-and-put-on-sale", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Commodity>> createAndPutOnSale(
            @Parameter(description = "商品名称", required = true)
            @RequestParam("commodityName") String commodityName,
            @Parameter(description = "商品描述", required = true)
            @RequestParam("commodityDescription") String commodityDescription,
            @Parameter(description = "商品分类ID", required = true)
            @RequestParam("categoryId") Integer categoryId,
            @Parameter(description = "商品标签ID列表", required = true)
            @RequestParam("tagsId") List<Integer> tagsId,
            @Parameter(description = "商品价格", required = true)
            @RequestParam("currentPrice") BigDecimal currentPrice,
            @Parameter(description = "商品数量", required = true)
            @RequestParam("quantity") Integer quantity,
            @Parameter(description = "卖家ID", required = true)
            @RequestParam("sellerId") String sellerId,
            @Parameter(description = "商品新旧度", required = true)
            @RequestParam("newness") String newness,
            @Parameter(description = "商品图片文件，支持多张图片上传", required = true)
            @RequestParam(value = "images", required = true) MultipartFile[] images) {
        
        logger.info("收到创建商品请求: {}", commodityName);
        
        try {
            // 详细记录接收到的参数信息
            logger.info("接收到的参数 - commodityName: {}, categoryId: {}, currentPrice: {}, quantity: {}, sellerId: {}, newness: {}", 
                       commodityName, categoryId, currentPrice, quantity, sellerId, newness);
            
            // 验证图片参数
            if (images == null) {
                logger.warn("images 参数为 null - 可能是前端未正确发送multipart数据");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("商品图片不能为空，请确保使用正确的 multipart/form-data 格式上传图片", "IMAGES_REQUIRED"));
            }
            
            // 记录图片数组信息
            logger.info("接收到的图片数量: {}", images.length);
            
            // 过滤掉空的文件项（前端可能发送空的文件字段）
            List<MultipartFile> validImages = new ArrayList<>();
            for (int i = 0; i < images.length; i++) {
                MultipartFile img = images[i];
                if (img != null && !img.isEmpty()) {
                    logger.info("有效图片 {}: 文件名={}, 大小={}, 类型={}", 
                               validImages.size(), img.getOriginalFilename(), img.getSize(), img.getContentType());
                    validImages.add(img);
                } else {
                    logger.warn("跳过空图片文件 {} (可能是前端发送的空字段)", i);
                }
            }
            
            // 检查是否有有效的图片文件
            if (validImages.isEmpty()) {
                logger.warn("没有接收到有效的图片文件");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("请至少上传一张商品图片", "NO_VALID_IMAGES"));
            }
            
            // 将有效图片转换回数组
            images = validImages.toArray(new MultipartFile[0]);
            logger.info("过滤后的有效图片数量: {}", images.length);
            
            // 手动验证必填参数
            if (commodityName == null || commodityName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("商品名称不能为空", "VALIDATION_ERROR"));
            }
            if (categoryId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("商品类别不能为空", "VALIDATION_ERROR"));
            }
            if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("商品价格必须大于0", "VALIDATION_ERROR"));
            }
            if (quantity == null || quantity <= 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("商品数量必须大于0", "VALIDATION_ERROR"));
            }
            if (sellerId == null || sellerId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("卖家ID不能为空", "VALIDATION_ERROR"));
            }
            if (newness == null || newness.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("商品新旧度不能为空", "VALIDATION_ERROR"));
            }
            
            // 验证图片文件格式（此时images已经过滤了空文件）
            for (int i = 0; i < images.length; i++) {
                MultipartFile image = images[i];
                // 验证文件类型
                String contentType = image.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    logger.warn("第{}张文件格式不正确: {}", (i + 1), contentType);
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("第" + (i + 1) + "张文件不是有效的图片格式，支持的格式：jpg, jpeg, png, gif, webp", "INVALID_IMAGE_FORMAT"));
                }
                
                // 验证文件大小（可选，防止过大文件）
                if (image.getSize() > 10 * 1024 * 1024) { // 10MB限制
                    logger.warn("第{}张图片文件过大: {} bytes", (i + 1), image.getSize());
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("第" + (i + 1) + "张图片文件过大，请上传小于10MB的图片", "IMAGE_TOO_LARGE"));
                }
            }
            
            // 创建请求对象
            CommodityCreateRequest request = new CommodityCreateRequest(
                    commodityName, commodityDescription, categoryId, tagsId,
                    currentPrice, null, null, quantity, sellerId, newness
            );
            
            Commodity commodity = commodityService.createAndPutOnSaleWithImages(request, images);
            logger.info("商品创建成功，商品ID: {}，状态: 待审核", commodity.getCommodityId());
            return ResponseEntity.ok(ApiResponse.success("商品创建成功，等待管理员审核", commodity));
        } catch (Exception e) {
            logger.error("商品创建异常: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("商品创建失败: " + e.getMessage(), "CREATE_FAILED"));
        }
    }
    
    /**
     * 申请上架商品（设置状态为待审核）
     * @param request 上架请求（只需要商品ID和卖家ID）
     * @param bindingResult 验证结果
     * @return 响应结果
     */
    @PostMapping("/put-on-sale")
    public ResponseEntity<ApiResponse<Void>> putOnSale(
            @Valid @RequestBody CommodityStatusUpdateRequest request,
            BindingResult bindingResult) {
        
        logger.info("收到申请上架商品请求: {}", request);
        
        // 参数验证
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            logger.warn("申请上架商品参数验证失败: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("参数验证失败: " + errorMessage, "VALIDATION_ERROR"));
        }
        
        try {
            boolean success = commodityService.putOnSale(request.getCommodityId(), request.getSellerId());
            
            if (success) {
                logger.info("商品申请上架成功，状态已设为待审核: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return ResponseEntity.ok(ApiResponse.success("商品申请上架成功，状态已设为待审核"));
            } else {
                logger.warn("商品申请上架失败，商品不存在或无权限: commodityId={}, sellerId={}", 
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
     * @param request 下架请求（只需要商品ID和卖家ID）
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
     * 标记商品为已售
     * @param request 商品状态更新请求（包含商品ID和卖家ID）
     * @param bindingResult 验证结果
     * @return 响应结果
     */
    @PostMapping("/mark-as-sold")
    public ResponseEntity<ApiResponse<Void>> markAsSold(
            @Valid @RequestBody CommodityStatusUpdateRequest request,
            BindingResult bindingResult) {
        
        logger.info("收到标记商品为已售请求: commodityId={}, sellerId={}", 
                   request.getCommodityId(), request.getSellerId());
        
        // 参数验证
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            logger.warn("标记商品为已售参数验证失败: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("参数验证失败: " + errorMessage, "VALIDATION_ERROR"));
        }
        
        try {
            boolean success = commodityService.markAsSold(
                request.getCommodityId(), 
                request.getSellerId()
            );
            
            if (success) {
                logger.info("商品标记为已售成功: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return ResponseEntity.ok(ApiResponse.success("商品已标记为已售"));
            } else {
                logger.warn("商品标记为已售失败，商品不存在或无权限: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("商品不存在或您无权限操作此商品", "COMMODITY_NOT_FOUND"));
            }
        } catch (Exception e) {
            logger.error("商品标记为已售异常: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("服务器内部错误: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 更新商品信息（名称、描述、价格、新旧度、数量、图片）
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @param commodityName 商品名称
     * @param commodityDescription 商品描述
     * @param currentPrice 商品价格
     * @param newness 商品新旧度
     * @param quantity 商品数量
     * @param images 商品图片文件（可选）
     * @return 响应结果
     */
    @Operation(summary = "更新商品信息", description = "更新商品的基本信息，支持直接上传新的商品图片")
    @PostMapping(value = "/update-info", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Void>> updateCommodityInfo(
            @Parameter(description = "商品ID", required = true)
            @RequestParam("commodityId") String commodityId,
            @Parameter(description = "卖家ID", required = true)
            @RequestParam("sellerId") String sellerId,
            @Parameter(description = "商品名称")
            @RequestParam(value = "commodityName", required = false) String commodityName,
            @Parameter(description = "商品描述")
            @RequestParam(value = "commodityDescription", required = false) String commodityDescription,
            @Parameter(description = "商品价格")
            @RequestParam(value = "currentPrice", required = false) BigDecimal currentPrice,
            @Parameter(description = "商品新旧度")
            @RequestParam(value = "newness", required = false) String newness,
            @Parameter(description = "商品数量")
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @Parameter(description = "商品图片文件，支持多张图片上传（可选）")
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        
        logger.info("收到更新商品信息请求: commodityId={}, sellerId={}", commodityId, sellerId);
        
        // 基本参数验证
        if (commodityId == null || commodityId.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("商品ID不能为空", "VALIDATION_ERROR"));
        }
        if (sellerId == null || sellerId.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("卖家ID不能为空", "VALIDATION_ERROR"));
        }
        
        // 验证价格
        if (currentPrice != null && currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("商品价格必须大于0", "VALIDATION_ERROR"));
        }
        
        // 验证数量
        if (quantity != null && quantity <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("商品数量必须大于0", "VALIDATION_ERROR"));
        }
        
        // 验证图片文件（如果提供）
        if (images != null && images.length > 0) {
            // 过滤掉空的文件项
            List<MultipartFile> validImages = new ArrayList<>();
            for (MultipartFile img : images) {
                if (img != null && !img.isEmpty()) {
                    validImages.add(img);
                }
            }
            
            // 验证图片格式和大小
            for (int i = 0; i < validImages.size(); i++) {
                MultipartFile image = validImages.get(i);
                String contentType = image.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    logger.warn("第{}张文件格式不正确: {}", (i + 1), contentType);
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("第" + (i + 1) + "张文件不是有效的图片格式，支持的格式：jpg, jpeg, png, gif, webp", "INVALID_IMAGE_FORMAT"));
                }
                
                if (image.getSize() > 10 * 1024 * 1024) { // 10MB限制
                    logger.warn("第{}张图片文件过大: {} bytes", (i + 1), image.getSize());
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("第" + (i + 1) + "张图片文件过大，请上传小于10MB的图片", "IMAGE_TOO_LARGE"));
                }
            }
            
            // 更新有效图片数组
            images = validImages.toArray(new MultipartFile[0]);
        }
        
        try {
            boolean success = commodityService.updateCommodityInfoWithImages(
                commodityId, sellerId, commodityName, commodityDescription, 
                currentPrice, newness, quantity, images);
            
            if (success) {
                logger.info("商品信息更新成功: commodityId={}, sellerId={}", commodityId, sellerId);
                return ResponseEntity.ok(ApiResponse.success("商品信息更新成功"));
            } else {
                logger.warn("商品信息更新失败，商品不存在或无权限: commodityId={}, sellerId={}", commodityId, sellerId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("商品不存在或您无权限操作此商品", "COMMODITY_NOT_FOUND"));
            }
        } catch (Exception e) {
            logger.error("商品信息更新异常: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("服务器内部错误: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 修改商品描述
     * @deprecated 此接口已废弃，请使用 /update-info 接口进行商品信息更新
     * @param request 修改请求
     * @param bindingResult 验证结果
     * @return 响应结果
     */
    @Deprecated
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
                case "to_sale":
                    commodityStatus = Commodity.CommodityStatus.TO_SALE;
                    break;
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
                            .body(ApiResponse.error("无效的商品状态，支持的状态: to_sale, on_sale, off_sale, sold", "INVALID_STATUS"));
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
     * 根据分类ID获取标签列表
     * @param categoryId 分类ID
     * @return 标签列表
     */
    @GetMapping("/tags")
    @Operation(summary = "根据分类ID获取标签列表", description = "根据输入的category_id，返回tags表的TID和tag_Name")
    public ResponseEntity<ApiResponse<List<TagDTO>>> getTagsByCategoryId(
            @Parameter(description = "分类ID", required = true)
            @RequestParam("category_id") Integer categoryId) {
        
        logger.info("收到获取标签列表请求: categoryId={}", categoryId);
        
        try {
            // 参数验证
            if (categoryId == null) {
                logger.warn("分类ID不能为空");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("分类ID不能为空", "CATEGORY_ID_REQUIRED"));
            }
            
            if (categoryId <= 0) {
                logger.warn("分类ID必须大于0: categoryId={}", categoryId);
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("分类ID必须大于0", "INVALID_CATEGORY_ID"));
            }
            
            // 调用服务层获取标签列表
            List<TagDTO> tags = tagService.getTagsByCategoryId(categoryId);
            
            logger.info("获取标签列表成功: categoryId={}, 标签数量={}", categoryId, tags.size());
            return ResponseEntity.ok(ApiResponse.success("获取标签列表成功", tags));
            
        } catch (IllegalArgumentException e) {
            logger.warn("参数错误: categoryId={}, error={}", categoryId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "INVALID_PARAMETER"));
        } catch (Exception e) {
            logger.error("获取标签列表异常: categoryId={}, error={}", categoryId, e.getMessage(), e);
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
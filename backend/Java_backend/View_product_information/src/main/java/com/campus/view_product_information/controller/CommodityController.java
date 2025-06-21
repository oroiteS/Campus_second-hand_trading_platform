package com.campus.view_product_information.controller;

import com.campus.view_product_information.dto.CommodityDetailDTO;
import com.campus.view_product_information.service.CommodityService;
import com.campus.view_product_information.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 商品信息控制器
 */
@Slf4j
@RestController
@RequestMapping("/api-8083/commodity")
@RequiredArgsConstructor
@Tag(name = "商品信息管理", description = "商品信息查看相关接口")
public class CommodityController {

    private final CommodityService commodityService;

    /**
     * 根据商品ID查看商品详情
     *
     * @param commodityId 商品ID
     * @return 商品详情信息
     */
    @GetMapping("/detail/{commodityId}")
    @Operation(summary = "查看商品详情", description = "根据商品ID获取商品的详细信息，包括商品基本信息和类别名称")
    public Result<CommodityDetailDTO> getCommodityDetail(
            @Parameter(description = "商品ID", required = true)
            @PathVariable String commodityId) {
        
        log.info("接收到查看商品详情请求，商品ID: {}", commodityId);
        
        try {
            CommodityDetailDTO commodityDetail = commodityService.getCommodityDetail(commodityId);
            
            if (commodityDetail == null) {
                log.warn("商品不存在，商品ID: {}", commodityId);
                return Result.notFound("商品不存在");
            }
            
            log.info("成功返回商品详情，商品名称: {}", commodityDetail.getCommodityName());
            return Result.success(commodityDetail);
            
        } catch (Exception e) {
            log.error("查询商品详情失败，商品ID: {}, 错误信息: {}", commodityId, e.getMessage(), e);
            return Result.error("查询商品详情失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     *
     * @return 服务状态
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查商品信息服务是否正常运行")
    public Result<String> health() {
        return Result.success("商品信息服务运行正常");
    }
}
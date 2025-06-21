package com.campus.view_latest.controller;

import com.campus.view_latest.common.Result;
import com.campus.view_latest.entity.Commodity;
import com.campus.view_latest.service.CommodityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品控制器
 */
@Slf4j
@RestController
@RequestMapping("/api-8087/commodities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "商品管理", description = "商品相关API")
public class CommodityController {
    
    private final CommodityService commodityService;
    
    /**
     * 获取最新6个在售商品的所有字段
     * @return 包含所有字段的商品列表
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新在售商品", description = "获取最新6个发布的在售商品的完整信息，包含所有字段")
    public Result<List<Commodity>> getLatestOnSaleCommodities() {
        log.info("接收到获取最新在售商品所有字段的请求");
        try {
            List<Commodity> commodities = commodityService.getLatestOnSaleCommodities();
            log.info("成功返回{}个商品的完整信息", commodities.size());
            return Result.success("获取最新商品成功", commodities);
        } catch (Exception e) {
            log.error("获取最新商品失败", e);
            return Result.error("获取商品信息失败: " + e.getMessage());
        }
    }
}
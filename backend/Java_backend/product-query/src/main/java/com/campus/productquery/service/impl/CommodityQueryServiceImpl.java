package com.campus.productquery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.productquery.dao.CommodityRepository;
import com.campus.productquery.dto.CategoryQueryRequest;
import com.campus.productquery.dto.CommodityResponse;
import com.campus.productquery.dto.PagedCommodityResponse;
import com.campus.productquery.pojo.Commodity;
import com.campus.productquery.service.CommodityQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品分类查询服务实现类
 * 专注于按分类查询商品，支持价格区间、新旧度筛选和排序功能
 */
@Service
public class CommodityQueryServiceImpl implements CommodityQueryService {

    @Autowired
    private CommodityRepository commodityRepository;

    @Override
    public PagedCommodityResponse getCommoditiesByCategory(CategoryQueryRequest request) {
        validateCategoryQueryRequest(request);
        
        // 构建查询条件
        QueryWrapper<Commodity> queryWrapper = buildCategoryQueryWrapper(request);
        
        // 分页查询
        Page<Commodity> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<Commodity> result = commodityRepository.selectPage(page, queryWrapper);
        
        // 转换为响应DTO
        List<CommodityResponse> commodityResponses = result.getRecords().stream()
                .map(CommodityResponse::new)
                .collect(Collectors.toList());
        
        return new PagedCommodityResponse(
                commodityResponses,
                request.getPageNum(),
                request.getPageSize(),
                result.getTotal()
        );
    }

    /**
     * 验证分类查询请求参数
     */
    private void validateCategoryQueryRequest(CategoryQueryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("查询请求不能为空");
        }
        
        if (request.getCategoryId() == null || request.getCategoryId() < 1 || request.getCategoryId() > 8) {
            throw new IllegalArgumentException("分类ID必须在1-8之间");
        }
        
        if (request.getPageNum() == null || request.getPageNum() < 1) {
            request.setPageNum(1);
        }
        
        if (request.getPageSize() == null || request.getPageSize() < 1 || request.getPageSize() > 100) {
            request.setPageSize(10);
        }
    }

    /**
     * 构建分类查询条件
     */
    private QueryWrapper<Commodity> buildCategoryQueryWrapper(CategoryQueryRequest request) {
        QueryWrapper<Commodity> queryWrapper = new QueryWrapper<>();
        
        // 类别ID（必须）
        queryWrapper.eq("category_id", request.getCategoryId());
        
        // 商品状态（默认查询在售商品）
        queryWrapper.eq("commodity_status", "on_sale");
        
        // 处理价格区间
        if (StringUtils.hasText(request.getPriceRange())) {
            switch (request.getPriceRange()) {
                case "0-50":
                    queryWrapper.le("current_price", 50);
                    break;
                case "50-200":
                    queryWrapper.between("current_price", 50, 200);
                    break;
                case "200-500":
                    queryWrapper.between("current_price", 200, 500);
                    break;
                case "500-1000":
                    queryWrapper.between("current_price", 500, 1000);
                    break;
                case "1000+":
                    queryWrapper.ge("current_price", 1000);
                    break;
            }
        }
        
        // 新旧度筛选
        if (StringUtils.hasText(request.getNewness())) {
            queryWrapper.eq("newness", request.getNewness());
        }
        
        // 排序处理
        String sortBy = StringUtils.hasText(request.getSortBy()) ? request.getSortBy() : "created_at";
        
        switch (sortBy) {
            case "price_asc":
                queryWrapper.orderByAsc("current_price");
                break;
            case "price_desc":
                queryWrapper.orderByDesc("current_price");
                break;
            case "time_asc":
                queryWrapper.orderByAsc("created_at");
                break;
            case "time_desc":
            default:
                queryWrapper.orderByDesc("created_at");
                break;
        }
        
        return queryWrapper;
    }
}
package com.example.view_product_information.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.view_product_information.dto.CommodityDetailDTO;
import com.example.view_product_information.entity.Commodity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商品Mapper接口
 */
@Mapper
public interface CommodityMapper extends BaseMapper<Commodity> {

    /**
     * 根据商品ID查询商品详情（包含类别名称）
     *
     * @param commodityId 商品ID
     * @return 商品详情
     */
    CommodityDetailDTO selectCommodityDetailById(@Param("commodityId") String commodityId);
}
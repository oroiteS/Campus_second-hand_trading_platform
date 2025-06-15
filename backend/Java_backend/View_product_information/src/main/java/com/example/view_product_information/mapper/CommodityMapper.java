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
    @Select("SELECT c.commodity_id, c.commodity_name, c.commodity_description, " +
            "c.category_id, cat.category as category_name, c.tags, c.current_price, " +
            "c.commodity_status, c.seller_id, c.main_image_url, c.image_list, " +
            "c.created_at, c.updated_at " +
            "FROM commodities c " +
            "LEFT JOIN categories cat ON c.category_id = cat.category_id " +
            "WHERE c.commodity_id = #{commodityId}")
    CommodityDetailDTO selectCommodityDetailById(@Param("commodityId") String commodityId);
}
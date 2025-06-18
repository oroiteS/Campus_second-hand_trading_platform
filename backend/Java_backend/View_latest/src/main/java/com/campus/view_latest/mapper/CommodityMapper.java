package com.campus.view_latest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.view_latest.entity.Commodity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品数据访问层
 */
@Mapper
public interface CommodityMapper extends BaseMapper<Commodity> {
    
    /**
     * 获取最新6个在售商品的所有字段
     * @return 商品列表
     */
    @Select("SELECT commodity_id, commodity_name, commodity_description, category_id, tags_Id, " +
            "current_price, commodity_status, seller_id, main_image_url, image_list, " +
            "created_at, updated_at, quantity, newness " +
            "FROM commodities WHERE commodity_status = 'on_sale' " +
            "ORDER BY created_at DESC LIMIT 6")
    List<Commodity> selectLatestOnSaleCommodities();
}
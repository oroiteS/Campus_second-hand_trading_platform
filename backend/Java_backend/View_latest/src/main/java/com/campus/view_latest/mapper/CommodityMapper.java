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
     * 获取最新6个在售商品
     * @return 商品列表
     */
    @Select("SELECT * FROM commodities WHERE commodity_status = 'on_sale' ORDER BY created_at DESC LIMIT 6")
    List<Commodity> selectLatestOnSaleCommodities();
}
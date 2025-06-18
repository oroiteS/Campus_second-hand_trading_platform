package com.campus.cart.dao;

import com.campus.cart.pojo.Commodity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommodityMapper {
    // 根据商品ID集合查所有商品
    @Select("<script>" +
            "SELECT * FROM commodities WHERE commodity_id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Commodity> getAllCommodities(@Param("ids") List<String> ids);

    // 根据商品ID集合和类别ID查商品
    @Select("<script>" +
            "SELECT * FROM commodities WHERE commodity_id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            " AND category_id = #{categoryId}" +
            "</script>")
    List<Commodity> getCommoditiesByCategory(@Param("ids") List<String> ids, @Param("categoryId") Integer categoryId);

    // 获取类别名对应的ID
    @Select("SELECT category_id FROM categories WHERE category = #{categoryName}")
    Integer getCategoryIdByName(String categoryName);
}


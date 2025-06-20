package com.campus.productquery.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.productquery.pojo.Commodity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品数据访问层接口
 * 专注于分类查询功能，继承BaseMapper提供基本的CRUD操作
 * 复杂查询通过QueryWrapper在Service层实现
 */
@Mapper
public interface CommodityRepository extends BaseMapper<Commodity> {
    // 继承BaseMapper提供的基本CRUD操作已足够
    // selectPage、selectCount等方法可直接使用
}
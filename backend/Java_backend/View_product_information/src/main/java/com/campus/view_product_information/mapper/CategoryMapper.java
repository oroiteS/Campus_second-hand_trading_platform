package com.campus.view_product_information.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.view_product_information.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品类别Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
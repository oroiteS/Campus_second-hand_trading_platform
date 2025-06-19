package com.campus.product_management_seller.repository;

import com.campus.product_management_seller.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Tag数据访问层接口
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    
    /**
     * 根据分类ID查询所有标签
     * @param categoryId 分类ID
     * @return 标签列表
     */
    @Query("SELECT t FROM Tag t WHERE t.categoryId = :categoryId")
    List<Tag> findByCategoryId(@Param("categoryId") Integer categoryId);
    
    /**
     * 根据分类ID查询所有标签（使用方法名查询）
     * @param categoryId 分类ID
     * @return 标签列表
     */
    List<Tag> findAllByCategoryId(Integer categoryId);
}
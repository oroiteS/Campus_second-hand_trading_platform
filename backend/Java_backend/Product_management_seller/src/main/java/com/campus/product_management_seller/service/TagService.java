package com.campus.product_management_seller.service;

import com.campus.product_management_seller.entity.Tag;
import com.campus.product_management_seller.dto.TagDTO;
import com.campus.product_management_seller.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务类
 */
@Service
@Transactional(readOnly = true)
public class TagService {
    
    private static final Logger logger = LoggerFactory.getLogger(TagService.class);
    
    @Autowired
    private TagRepository tagRepository;
    
    /**
     * 根据分类ID获取标签列表
     * @param categoryId 分类ID
     * @return 标签DTO列表
     */
    public List<TagDTO> getTagsByCategoryId(Integer categoryId) {
        logger.info("根据分类ID获取标签列表，categoryId: {}", categoryId);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        try {
            List<Tag> tags = tagRepository.findAllByCategoryId(categoryId);
            logger.info("查询到 {} 个标签，categoryId: {}", tags.size(), categoryId);
            
            // 将Tag实体转换为TagDTO
            List<TagDTO> tagDTOs = tags.stream()
                    .map(tag -> new TagDTO(tag.getTid(), tag.getTagName()))
                    .collect(Collectors.toList());
            
            return tagDTOs;
        } catch (Exception e) {
            logger.error("根据分类ID获取标签列表失败，categoryId: {}, 错误: {}", categoryId, e.getMessage(), e);
            throw new RuntimeException("获取标签列表失败: " + e.getMessage(), e);
        }
    }
}
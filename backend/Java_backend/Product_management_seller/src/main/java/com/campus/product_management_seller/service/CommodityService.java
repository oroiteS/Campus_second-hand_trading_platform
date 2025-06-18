package com.campus.product_management_seller.service;

import com.campus.product_management_seller.repository.CommodityRepository;
import com.campus.product_management_seller.entity.Commodity;
import com.campus.product_management_seller.dto.CommodityCreateRequest;
import com.campus.product_management_seller.dto.CommodityUpdateRequest;
import com.campus.product_management_seller.util.OssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CommodityService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommodityService.class);
    
    @Autowired
    private CommodityRepository commodityRepository;
    
    @Autowired
    private OssUtil ossUtil;
    
    /**
     * 创建并上架商品
     * @param request 商品创建请求
     * @return 创建的商品信息
     */
    public Commodity createAndPutOnSale(CommodityCreateRequest request) {
        logger.info("尝试创建并上架商品: {}", request);
        
        try {
            // 生成随机商品ID
            String commodityId = UUID.randomUUID().toString();
            
            // 处理图片URL，根据新的命名规则
            String processedMainImageUrl = commodityId + "_main.jpg";
            String processedImageList = processImageList(request.getImageList(), commodityId);
            
            // 创建商品实体
            Commodity commodity = new Commodity();
            commodity.setCommodityId(commodityId);
            commodity.setCommodityName(request.getCommodityName());
            commodity.setCommodityDescription(request.getCommodityDescription());
            commodity.setCategoryId(request.getCategoryId());
            commodity.setTagsId(processTagsId(request.getTagsId()));
            commodity.setCurrentPrice(request.getCurrentPrice());
            commodity.setMainImageUrl(processedMainImageUrl);
            commodity.setImageList(processedImageList);
            commodity.setQuantity(request.getQuantity());
            commodity.setSellerId(request.getSellerId());
            commodity.setNewness(request.getNewness());
            commodity.setCommodityStatus(Commodity.CommodityStatus.ON_SALE);
            commodity.setCreatedAt(LocalDateTime.now());
            commodity.setUpdatedAt(LocalDateTime.now());
            
            // 保存到数据库
            Commodity savedCommodity = commodityRepository.save(commodity);
            
            logger.info("商品创建并上架成功: commodityId={}, sellerId={}", commodityId, request.getSellerId());
            return savedCommodity;
            
        } catch (Exception e) {
            logger.error("商品创建并上架异常: sellerId={}, error={}", request.getSellerId(), e.getMessage(), e);
            throw new RuntimeException("商品创建并上架失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 处理标签ID，将字符串转换为JSON数组格式
     * @param tagsId 原始标签字符串（逗号分隔）
     * @return JSON格式的标签数组
     */
    private String processTagsId(String tagsId) {
        if (tagsId == null || tagsId.trim().isEmpty()) {
            return null;
        }
        
        // 如果已经是JSON格式，直接返回
        if (tagsId.startsWith("[") && tagsId.endsWith("]")) {
            return tagsId;
        }
        
        // 将逗号分隔的字符串转换为JSON数组
        String[] tags = tagsId.split(",");
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < tags.length; i++) {
            if (i > 0) {
                result.append(",");
            }
            result.append("\"").append(tags[i].trim()).append("\"");
        }
        result.append("]");
        
        return result.toString();
    }
    
    /**
     * 处理图片列表，根据新的命名规则生成图片文件名
     * @param imageList 原始图片列表（可能是JSON格式或逗号分隔的字符串）
     * @param commodityId 商品ID
     * @return 处理后的图片列表
     */
    private String processImageList(String imageList, String commodityId) {
        if (imageList == null || imageList.trim().isEmpty()) {
            return null; // 如果没有额外图片，返回null
        }
        
        // 简单处理：假设imageList包含多个图片URL，用逗号分隔或JSON数组格式
        // 这里生成标准化的文件名：commodity_id_2.jpg, commodity_id_3.jpg, ...
        
        // 计算图片数量（简单估算，实际应该解析具体格式）
        String[] images;
        if (imageList.startsWith("[") && imageList.endsWith("]")) {
            // JSON数组格式，简单处理
            String content = imageList.substring(1, imageList.length() - 1);
            images = content.split(",");
        } else {
            // 逗号分隔格式
            images = imageList.split(",");
        }
        
        if (images.length == 0) {
            return null;
        }
        
        // 生成新的文件名列表
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < images.length; i++) {
            if (i > 0) {
                result.append(",");
            }
            result.append("\"").append(commodityId).append("_").append(i + 2).append(".jpg\"");
        }
        result.append("]");
        
        return result.toString();
    }
    
    /**
     * 创建并上架商品（支持图片上传）
     * @param request 商品创建请求
     * @param images 商品图片文件数组
     * @return 创建的商品信息
     */
    public Commodity createAndPutOnSaleWithImages(CommodityCreateRequest request, MultipartFile[] images) {
        logger.info("尝试创建并上架商品（带图片上传）: {}", request.getCommodityName());
        
        try {
            // 生成随机商品ID
            String commodityId = UUID.randomUUID().toString();
            
            // 上传图片到OSS并获取URL列表
            List<String> imageUrls = ossUtil.uploadCommodityImages(Arrays.asList(images), request.getSellerId());
            
            // 处理图片URL，添加https前缀
            String mainImageUrl = null;
            String imageListJson = null;
            
            if (imageUrls != null && !imageUrls.isEmpty()) {
                // 为所有URL添加https前缀
                List<String> httpsImageUrls = new ArrayList<>();
                for (String url : imageUrls) {
                    String httpsUrl = "https://" + url;
                    httpsImageUrls.add(httpsUrl);
                }
                
                // 第一张图片作为主图
                mainImageUrl = httpsImageUrls.get(0);
                
                // 如果有多张图片，将所有图片URL保存到imageList
                if (httpsImageUrls.size() > 1) {
                    StringBuilder imageListBuilder = new StringBuilder("[");
                    for (int i = 0; i < httpsImageUrls.size(); i++) {
                        if (i > 0) {
                            imageListBuilder.append(",");
                        }
                        imageListBuilder.append("\"").append(httpsImageUrls.get(i)).append("\"");
                    }
                    imageListBuilder.append("]");
                    imageListJson = imageListBuilder.toString();
                } else {
                    // 只有一张图片时，imageList保存该图片URL的JSON数组格式
                    imageListJson = "[\"" + mainImageUrl + "\"]";
                }
            }
            
            // 创建商品实体
            Commodity commodity = new Commodity();
            commodity.setCommodityId(commodityId);
            commodity.setCommodityName(request.getCommodityName());
            commodity.setCommodityDescription(request.getCommodityDescription());
            commodity.setCategoryId(request.getCategoryId());
            commodity.setTagsId(processTagsId(request.getTagsId()));
            commodity.setCurrentPrice(request.getCurrentPrice());
            commodity.setMainImageUrl(mainImageUrl);
            commodity.setImageList(imageListJson);
            commodity.setQuantity(request.getQuantity());
            commodity.setSellerId(request.getSellerId());
            commodity.setNewness(request.getNewness());
            commodity.setCommodityStatus(Commodity.CommodityStatus.ON_SALE);
            commodity.setCreatedAt(LocalDateTime.now());
            commodity.setUpdatedAt(LocalDateTime.now());
            
            // 保存到数据库
            Commodity savedCommodity = commodityRepository.save(commodity);
            
            logger.info("商品创建并上架成功（带图片上传）: commodityId={}, sellerId={}, 图片数量={}", 
                       commodityId, request.getSellerId(), imageUrls != null ? imageUrls.size() : 0);
            return savedCommodity;
            
        } catch (Exception e) {
            logger.error("商品创建并上架异常（带图片上传）: sellerId={}, error={}", request.getSellerId(), e.getMessage(), e);
            throw new RuntimeException("商品创建并上架失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 上架已存在商品（原有功能保留）
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 是否成功
     */
    public boolean putOnSale(String commodityId, String sellerId) {
        logger.info("尝试上架商品: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            int updatedRows = commodityRepository.updateCommodityStatus(
                commodityId, sellerId, Commodity.CommodityStatus.ON_SALE);
            
            if (updatedRows > 0) {
                logger.info("商品上架成功: commodityId={}, sellerId={}", commodityId, sellerId);
                return true;
            } else {
                logger.warn("商品上架失败，未找到对应商品: commodityId={}, sellerId={}", commodityId, sellerId);
                return false;
            }
        } catch (Exception e) {
            logger.error("商品上架异常: commodityId={}, sellerId={}, error={}", commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("商品上架失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 下架商品
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 是否成功
     */
    public boolean putOffSale(String commodityId, String sellerId) {
        logger.info("尝试下架商品: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            int updatedRows = commodityRepository.updateCommodityStatus(
                commodityId, sellerId, Commodity.CommodityStatus.OFF_SALE);
            
            if (updatedRows > 0) {
                logger.info("商品下架成功: commodityId={}, sellerId={}", commodityId, sellerId);
                return true;
            } else {
                logger.warn("商品下架失败，未找到对应商品: commodityId={}, sellerId={}", commodityId, sellerId);
                return false;
            }
        } catch (Exception e) {
            logger.error("商品下架异常: commodityId={}, sellerId={}, error={}", commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("商品下架失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新商品信息（名称、价格、新旧度）
     * @param request 商品更新请求
     * @return 是否更新成功
     */
    public boolean updateCommodityInfo(CommodityUpdateRequest request) {
        logger.info("尝试更新商品信息: {}", request);
        
        try {
            Optional<Commodity> commodityOpt = commodityRepository.findByCommodityIdAndSellerId(
                request.getCommodityId(), request.getSellerId());
            
            if (!commodityOpt.isPresent()) {
                logger.warn("未找到对应商品: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return false;
            }
            
            Commodity commodity = commodityOpt.get();
            boolean updated = false;
            
            // 更新商品名称
            if (request.getCommodityName() != null && !request.getCommodityName().trim().isEmpty()) {
                commodity.setCommodityName(request.getCommodityName());
                updated = true;
            }
            
            // 更新商品描述
            if (request.getCommodityDescription() != null) {
                commodity.setCommodityDescription(request.getCommodityDescription());
                updated = true;
            }
            
            // 更新商品价格
            if (request.getCurrentPrice() != null) {
                commodity.setCurrentPrice(request.getCurrentPrice());
                updated = true;
            }
            
            // 更新商品新旧度
            if (request.getNewness() != null && !request.getNewness().trim().isEmpty()) {
                commodity.setNewness(request.getNewness());
                updated = true;
            }
            
            if (updated) {
                commodity.setUpdatedAt(LocalDateTime.now());
                commodityRepository.save(commodity);
                logger.info("商品信息更新成功: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return true;
            } else {
                logger.info("没有需要更新的字段: commodityId={}, sellerId={}", 
                           request.getCommodityId(), request.getSellerId());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("商品信息更新异常: commodityId={}, sellerId={}, error={}", 
                        request.getCommodityId(), request.getSellerId(), e.getMessage(), e);
            throw new RuntimeException("商品信息更新失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 修改商品描述
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @param description 新的商品描述
     * @return 是否修改成功
     */
    public boolean updateDescription(String commodityId, String sellerId, String description) {
        logger.info("尝试修改商品描述: commodityId={}, sellerId={}, description={}", 
                   commodityId, sellerId, description);
        
        try {
            int updatedRows = commodityRepository.updateCommodityDescription(
                commodityId, sellerId, description);
            
            if (updatedRows > 0) {
                logger.info("商品描述修改成功: commodityId={}, sellerId={}", commodityId, sellerId);
                return true;
            } else {
                logger.warn("商品描述修改失败，未找到对应商品: commodityId={}, sellerId={}", commodityId, sellerId);
                return false;
            }
        } catch (Exception e) {
            logger.error("商品描述修改异常: commodityId={}, sellerId={}, error={}", 
                        commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("商品描述修改失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取卖家的所有商品
     * @param sellerId 卖家ID
     * @return 商品列表
     */
    @Transactional(readOnly = true)
    public List<Commodity> getCommoditiesBySeller(String sellerId) {
        logger.info("获取卖家商品列表: sellerId={}", sellerId);
        
        try {
            List<Commodity> commodities = commodityRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
            logger.info("获取到{}个商品: sellerId={}", commodities.size(), sellerId);
            return commodities;
        } catch (Exception e) {
            logger.error("获取卖家商品列表异常: sellerId={}, error={}", sellerId, e.getMessage(), e);
            throw new RuntimeException("获取商品列表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据状态获取卖家的商品
     * @param sellerId 卖家ID
     * @param status 商品状态
     * @return 商品列表
     */
    @Transactional(readOnly = true)
    public List<Commodity> getCommoditiesBySellerAndStatus(String sellerId, Commodity.CommodityStatus status) {
        logger.info("根据状态获取卖家商品列表: sellerId={}, status={}", sellerId, status);
        
        try {
            List<Commodity> commodities = commodityRepository.findBySellerIdAndCommodityStatusOrderByCreatedAtDesc(sellerId, status);
            logger.info("获取到{}个商品: sellerId={}, status={}", commodities.size(), sellerId, status);
            return commodities;
        } catch (Exception e) {
            logger.error("根据状态获取卖家商品列表异常: sellerId={}, status={}, error={}", 
                        sellerId, status, e.getMessage(), e);
            throw new RuntimeException("获取商品列表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取商品详情
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 商品信息
     */
    @Transactional(readOnly = true)
    public Optional<Commodity> getCommodityDetail(String commodityId, String sellerId) {
        logger.info("获取商品详情: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            Optional<Commodity> commodity = commodityRepository.findByCommodityIdAndSellerId(commodityId, sellerId);
            if (commodity.isPresent()) {
                logger.info("获取商品详情成功: commodityId={}, sellerId={}", commodityId, sellerId);
            } else {
                logger.warn("未找到商品: commodityId={}, sellerId={}", commodityId, sellerId);
            }
            return commodity;
        } catch (Exception e) {
            logger.error("获取商品详情异常: commodityId={}, sellerId={}, error={}", 
                        commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("获取商品详情失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查商品是否属于指定卖家
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 是否属于
     */
    @Transactional(readOnly = true)
    public boolean isCommodityOwnedBySeller(String commodityId, String sellerId) {
        logger.debug("检查商品归属: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            boolean exists = commodityRepository.existsByCommodityIdAndSellerId(commodityId, sellerId);
            logger.debug("商品归属检查结果: commodityId={}, sellerId={}, exists={}", 
                        commodityId, sellerId, exists);
            return exists;
        } catch (Exception e) {
            logger.error("检查商品归属异常: commodityId={}, sellerId={}, error={}", 
                        commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("检查商品归属失败: " + e.getMessage(), e);
        }
    }
}
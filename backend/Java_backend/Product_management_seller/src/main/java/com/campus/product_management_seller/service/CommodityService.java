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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            commodity.setCommodityStatus(Commodity.CommodityStatus.TO_SALE);
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
     * 处理标签ID，将List<Integer>转换为JSON数组格式
     * @param tagsId 标签ID列表
     * @return JSON格式的标签数组
     */
    private String processTagsId(List<Integer> tagsId) {
        if (tagsId == null || tagsId.isEmpty()) {
            return null;
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(tagsId);
        } catch (Exception e) {
            logger.error("转换标签ID列表为JSON失败: {}", e.getMessage());
            return null;
        }
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
            commodity.setCommodityStatus(Commodity.CommodityStatus.TO_SALE);
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
     * 申请上架商品（设置状态为待审核）
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 是否成功
     */
    public boolean putOnSale(String commodityId, String sellerId) {
        logger.info("尝试申请上架商品: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            int updatedRows = commodityRepository.updateCommodityStatus(
                commodityId, sellerId, Commodity.CommodityStatus.TO_SALE);
            
            if (updatedRows > 0) {
                logger.info("商品申请上架成功，状态已设为待审核: commodityId={}, sellerId={}", commodityId, sellerId);
                return true;
            } else {
                logger.warn("商品申请上架失败，未找到对应商品: commodityId={}, sellerId={}", commodityId, sellerId);
                return false;
            }
        } catch (Exception e) {
            logger.error("商品申请上架异常: commodityId={}, sellerId={}, error={}", commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("商品申请上架失败: " + e.getMessage(), e);
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
     * 标记商品为已售
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @return 是否成功
     */
    public boolean markAsSold(String commodityId, String sellerId) {
        logger.info("尝试标记商品为已售: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            int updatedRows = commodityRepository.updateCommodityStatus(
                commodityId, sellerId, Commodity.CommodityStatus.SOLD);
            
            if (updatedRows > 0) {
                logger.info("商品标记为已售成功: commodityId={}, sellerId={}", commodityId, sellerId);
                return true;
            } else {
                logger.warn("商品标记为已售失败，未找到对应商品: commodityId={}, sellerId={}", commodityId, sellerId);
                return false;
            }
        } catch (Exception e) {
            logger.error("商品标记为已售异常: commodityId={}, sellerId={}, error={}", commodityId, sellerId, e.getMessage(), e);
            throw new RuntimeException("商品标记为已售失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新商品信息（支持图片上传）
     * @param commodityId 商品ID
     * @param sellerId 卖家ID
     * @param commodityName 商品名称
     * @param commodityDescription 商品描述
     * @param currentPrice 商品价格
     * @param newness 商品新旧度
     * @param quantity 商品数量
     * @param deleteExistingImages 是否删除原有图片
     * @param images 商品图片文件数组（可选）
     * @return 是否更新成功
     */
    public boolean updateCommodityInfoWithImages(String commodityId, String sellerId, 
                                               String commodityName, String commodityDescription,
                                               BigDecimal currentPrice, String newness, 
                                               Integer quantity, Boolean deleteExistingImages, MultipartFile[] images) {
        logger.info("尝试更新商品信息（支持图片上传）: commodityId={}, sellerId={}", commodityId, sellerId);
        
        try {
            Optional<Commodity> commodityOpt = commodityRepository.findByCommodityIdAndSellerId(
                commodityId, sellerId);
            
            if (!commodityOpt.isPresent()) {
                logger.warn("未找到对应商品: commodityId={}, sellerId={}", commodityId, sellerId);
                return false;
            }
            
            Commodity commodity = commodityOpt.get();
            boolean updated = false;
            
            // 更新商品名称
            if (commodityName != null && !commodityName.trim().isEmpty()) {
                commodity.setCommodityName(commodityName);
                updated = true;
            }
            
            // 更新商品描述
            if (commodityDescription != null) {
                commodity.setCommodityDescription(commodityDescription);
                updated = true;
            }
            
            // 更新商品价格
            if (currentPrice != null) {
                commodity.setCurrentPrice(currentPrice);
                updated = true;
            }
            
            // 更新商品新旧度
            if (newness != null && !newness.trim().isEmpty()) {
                commodity.setNewness(newness);
                updated = true;
            }
            
            // 更新商品数量
            if (quantity != null) {
                commodity.setQuantity(quantity);
                updated = true;
            }
            
            // 处理图片更新逻辑
            boolean imageUpdated = false;
            List<String> finalImageUrls = new ArrayList<>();
            
            // 如果不删除原有图片，先获取原有图片列表
            if (deleteExistingImages == null || !deleteExistingImages) {
                String existingImageList = commodity.getImageList();
                if (existingImageList != null && !existingImageList.trim().isEmpty() && !"[]".equals(existingImageList.trim())) {
                    try {
                        // 解析现有图片JSON数组
                        String cleanImageList = existingImageList.trim();
                        if (cleanImageList.startsWith("[") && cleanImageList.endsWith("]")) {
                            cleanImageList = cleanImageList.substring(1, cleanImageList.length() - 1);
                            if (!cleanImageList.trim().isEmpty()) {
                                String[] existingUrls = cleanImageList.split(",");
                                for (String url : existingUrls) {
                                    String cleanUrl = url.trim().replaceAll("^\"|\"$", "");
                                    if (!cleanUrl.isEmpty()) {
                                        finalImageUrls.add(cleanUrl);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("解析现有图片列表失败: commodityId={}, imageList={}, error={}", 
                                   commodityId, existingImageList, e.getMessage());
                    }
                }
            }
            
            // 如果有新图片，上传并添加到列表
            if (images != null && images.length > 0) {
                List<String> newImageUrls = ossUtil.uploadCommodityImages(Arrays.asList(images), sellerId);
                
                if (newImageUrls != null && !newImageUrls.isEmpty()) {
                    // 为新图片URL添加https前缀
                    for (String url : newImageUrls) {
                        String httpsUrl = "https://" + url;
                        finalImageUrls.add(httpsUrl);
                    }
                    imageUpdated = true;
                    logger.info("新增商品图片成功: commodityId={}, 新增图片数量={}", commodityId, newImageUrls.size());
                }
            }
            
            // 如果删除了原有图片或有新图片，更新图片信息
            if ((deleteExistingImages != null && deleteExistingImages) || imageUpdated) {
                if (!finalImageUrls.isEmpty()) {
                    // 第一张图片作为主图
                    String mainImageUrl = finalImageUrls.get(0);
                    commodity.setMainImageUrl(mainImageUrl);
                    
                    // 构建图片列表JSON
                    StringBuilder imageListBuilder = new StringBuilder("[");
                    for (int i = 0; i < finalImageUrls.size(); i++) {
                        if (i > 0) {
                            imageListBuilder.append(",");
                        }
                        imageListBuilder.append("\"").append(finalImageUrls.get(i)).append("\"");
                    }
                    imageListBuilder.append("]");
                    commodity.setImageList(imageListBuilder.toString());
                    
                    logger.info("商品图片更新成功: commodityId={}, 最终图片数量={}", commodityId, finalImageUrls.size());
                } else {
                    // 如果删除了所有图片且没有新图片，清空图片字段
                    commodity.setMainImageUrl(null);
                    commodity.setImageList("[]");
                    logger.info("商品图片已清空: commodityId={}", commodityId);
                }
                updated = true;
            }
            
            if (updated) {
                commodity.setUpdatedAt(LocalDateTime.now());
                commodityRepository.save(commodity);
                logger.info("商品信息更新成功: commodityId={}, sellerId={}", commodityId, sellerId);
                return true;
            } else {
                logger.info("没有需要更新的字段: commodityId={}, sellerId={}", commodityId, sellerId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("商品信息更新异常: commodityId={}, sellerId={}, error={}", 
                        commodityId, sellerId, e.getMessage(), e);
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
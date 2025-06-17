-- 创建校园二手交易平台商品表
-- 基于商品表结构设计文档

USE campus;

-- 首先创建商品类别表（如果不存在）
CREATE TABLE IF NOT EXISTS `categories` (
    `category_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '商品类别ID',
    `category_name` VARCHAR(50) NOT NULL COMMENT '类别名称',
    `description` TEXT DEFAULT NULL COMMENT '类别描述',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    UNIQUE KEY `uk_category_name` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品类别表';

-- 创建商品表
CREATE TABLE IF NOT EXISTS `commodities` (
    `commodity_id` CHAR(36) NOT NULL PRIMARY KEY COMMENT '商品唯一标识符（UUIDv7）',
    `commodity_name` VARCHAR(100) NOT NULL COMMENT '商品标题（如"苹果iPhone 13"）',
    `commodity_description` TEXT DEFAULT NULL COMMENT '详细描述（颜色、瑕疵等）',
    `category_id` INT UNSIGNED NOT NULL COMMENT '关联类别表的外键',
    `tags_Id` JSON DEFAULT NULL COMMENT '存储标签ID数组（如[1,2]）',
    `current_price` DECIMAL(10,2) NOT NULL COMMENT '商品售价（如3500.00）',
    `commodity_status` ENUM('on_sale', 'sold', 'off_sale') NOT NULL DEFAULT 'on_sale' COMMENT '商品状态：在售/已售/下架',
    `seller_id` CHAR(9) NOT NULL COMMENT '关联用户表的外键',
    `main_image_url` VARCHAR(255) DEFAULT NULL COMMENT '商品主图链接',
    `image_list` JSON DEFAULT NULL COMMENT '多图链接数组（可选）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '商品发布时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '信息更新时间',
    `quantity` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '商品数量',
    
    -- 创建索引
    INDEX `idx_commodity_name` (`commodity_name`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_current_price` (`current_price`),
    INDEX `idx_commodity_status` (`commodity_status`),
    INDEX `idx_seller_id` (`seller_id`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_updated_at` (`updated_at`),
    
    -- 复合索引（优化常用查询）
    INDEX `idx_status_price` (`commodity_status`, `current_price`),
    INDEX `idx_category_status` (`category_id`, `commodity_status`),
    INDEX `idx_seller_status` (`seller_id`, `commodity_status`),
    INDEX `idx_status_time` (`commodity_status`, `created_at`),
    INDEX `idx_status_quantity` (`commodity_status`, `quantity`),
    
    -- 外键约束
    CONSTRAINT `fk_commodities_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_commodities_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- 检查约束
    CONSTRAINT `chk_price_positive` CHECK (`current_price` > 0)
    CONSTRAINT `chk_quantity_non_negative` CHECK (`quantity` >= 0)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 插入一些基础商品类别数据
INSERT INTO `categories` (`category_name`, `description`) VALUES
('电子产品', '手机、电脑、平板等电子设备'),
('图书教材', '教科书、参考书、小说等'),
('生活用品', '日用品、化妆品、服装等'),
('运动器材', '健身器材、球类、户外用品等'),
('其他', '其他未分类商品')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- 显示表结构
DESCRIBE categories;
DESCRIBE commodities;

-- 验证表结构
SELECT 
    TABLE_NAME as '表名',
    COLUMN_NAME as '字段名',
    DATA_TYPE as '类型',
    IS_NULLABLE as '是否可为空',
    COLUMN_DEFAULT as '默认值',
    COLUMN_COMMENT as '描述'
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'campus' AND TABLE_NAME IN ('categories', 'commodities')
ORDER BY TABLE_NAME, ORDINAL_POSITION;

-- 查看索引信息
SHOW INDEX FROM commodities;

-- 示例查询（验证索引效果）
/*
-- 按类别和状态查询商品
SELECT commodity_id, commodity_name, current_price 
FROM commodities 
WHERE category_id = 1 AND commodity_status = 'on_sale' 
ORDER BY created_at DESC;

-- 按价格范围查询在售商品
SELECT commodity_id, commodity_name, current_price 
FROM commodities 
WHERE commodity_status = 'on_sale' AND current_price BETWEEN 1000 AND 5000 
ORDER BY current_price ASC;

-- 查询某卖家的所有商品
SELECT commodity_id, commodity_name, commodity_status, current_price 
FROM commodities 
WHERE seller_id = '202100001' 
ORDER BY created_at DESC;
*/
-- 创建校园二手交易平台商品类别表
-- 基于商品类别表结构设计文档

USE campus;

-- 创建商品类别表
CREATE TABLE IF NOT EXISTS `categories` (
    `商品类ID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '商品类别ID，主键',
    `商品类` VARCHAR(64) NOT NULL COMMENT '存储商品具体类别',
    
    -- 创建索引
    INDEX `idx_category_name` (`商品类`),
    
    -- 添加唯一约束
    UNIQUE KEY `uk_category_name` (`商品类`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品类别表';

-- 插入一些基础商品类别数据
INSERT INTO `categories` (`商品类`) VALUES
('电子产品'),
('图书教材'),
('生活用品'),
('运动器材'),
('服装鞋帽'),
('美妆护肤'),
('食品饮料'),
('家居用品'),
('文具办公'),
('其他')
ON DUPLICATE KEY UPDATE `商品类` = VALUES(`商品类`);

-- 显示表结构
DESCRIBE categories;

-- 验证表结构
SELECT 
    TABLE_NAME as '表名',
    COLUMN_NAME as '字段名',
    DATA_TYPE as '类型',
    IS_NULLABLE as '是否可为空',
    COLUMN_DEFAULT as '默认值',
    COLUMN_COMMENT as '描述'
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'campus' AND TABLE_NAME = 'categories'
ORDER BY ORDINAL_POSITION;

-- 查看索引信息
SHOW INDEX FROM categories;

-- 示例查询
/*
-- 查询所有商品类别
SELECT * FROM categories ORDER BY 商品类ID;

-- 根据类别名称查询
SELECT * FROM categories WHERE 商品类 = '电子产品';

-- 统计各类别下的商品数量（需要先有商品表）
SELECT c.商品类, COUNT(co.commodity_id) as 商品数量
FROM categories c
LEFT JOIN commodities co ON c.商品类ID = co.category_id
GROUP BY c.商品类ID, c.商品类
ORDER BY 商品数量 DESC;
*/
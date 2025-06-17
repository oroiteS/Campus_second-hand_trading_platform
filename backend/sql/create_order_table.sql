-- 创建校园二手交易平台订单表
-- 基于需求分析文档中的订单表结构

USE campus;

CREATE TABLE IF NOT EXISTS `orders` (
    `order_id` CHAR(36) NOT NULL PRIMARY KEY COMMENT 'uuidv7，主键，订单ID',
    `commodity_id` CHAR(36) NOT NULL COMMENT '外键，与商品表关联',
    `Buyer_id` CHAR(9) NOT NULL COMMENT '买家id',
    `Seller_id` CHAR(9) NOT NULL COMMENT '卖家id',
    `order_status` ENUM('pending_payment', 'pending_transaction', 'completed') NOT NULL DEFAULT 'pending_payment' COMMENT '订单状态：pending_payment=代付款，pending_transaction=代交易，completed=已完成',
    `Sale_time` DATETIME DEFAULT NULL COMMENT '交易时间',
    `Money` DECIMAL(12,2) DEFAULT NULL COMMENT '交易金额',
    `Sale_loc` VARCHAR(250) DEFAULT NULL COMMENT '交易地址',
    `buy_quantity` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '购买数量',
    `refund_reason` TEXT DEFAULT NULL COMMENT '退款理由',



    -- 创建索引
    INDEX `idx_commodity_id` (`commodity_id`),
    INDEX `idx_buyer_id` (`Buyer_id`),
    INDEX `idx_seller_id` (`Seller_id`),
    INDEX `idx_order_status` (`order_status`),
    INDEX `idx_sale_time` (`Sale_time`),
    INDEX `idx_money` (`Money`),
    INDEX `idx_buyer_time` (`Buyer_id`, `Sale_time`),
    INDEX `idx_seller_time` (`Seller_id`, `Sale_time`),
    INDEX `idx_commodity_time` (`commodity_id`, `Sale_time`),
    INDEX `idx_buyer_status` (`Buyer_id`, `order_status`),
    INDEX `idx_seller_status` (`Seller_id`, `order_status`),

    -- 外键约束
    CONSTRAINT `fk_orders_commodity` FOREIGN KEY (`commodity_id`) REFERENCES `commodities` (`commodity_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_orders_buyer` FOREIGN KEY (`Buyer_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_orders_seller` FOREIGN KEY (`Seller_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 显示表结构
DESCRIBE orders;

-- 验证表结构
SELECT 
    COLUMN_NAME as '字段名',
    DATA_TYPE as '类型',
    IS_NULLABLE as '是否可为空',
    COLUMN_DEFAULT as '默认值',
    COLUMN_COMMENT as '描述'
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'campus' AND TABLE_NAME = 'orders'
ORDER BY ORDINAL_POSITION;
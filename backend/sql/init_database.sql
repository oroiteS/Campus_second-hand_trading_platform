-- 校园二手交易平台数据库初始化脚本
-- 包含数据库创建、用户创建和表创建的完整流程

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS campus
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;


-- 2. 创建数据库用户（如果不存在）
CREATE USER IF NOT EXISTS 'campus_test'@'%' IDENTIFIED BY 'campus_suep';

-- 3. 授予用户权限
GRANT ALL PRIVILEGES ON campus.* TO 'campus_test'@'%';
FLUSH PRIVILEGES;

-- 4. 使用数据库
USE campus;

-- 5. 创建用户表
CREATE TABLE IF NOT EXISTS `users` (
    `User_ID` CHAR(9) NOT NULL PRIMARY KEY COMMENT '主键，用户ID',
    `User_name` VARCHAR(20) NOT NULL COMMENT '存储用户名',
    `password` VARCHAR(64) NOT NULL COMMENT 'sha256加密后的密码',
    `telephone` CHAR(11) NOT NULL COMMENT '用户电话号',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `avatar_url` VARCHAR(2048) DEFAULT NULL COMMENT '头像url（默认地址：）',
    `User_Loc_longitude` DECIMAL(9,6) NOT NULL DEFAULT 121.891585 COMMENT '存储经度',
    `User_sta` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否封号（默认否）',
    `Create_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `ID` CHAR(18) NOT NULL COMMENT '身份证号',
    `User_Loc_latitude` DECIMAL(9,6) NOT NULL DEFAULT 30.901871 COMMENT '存储纬度',

    -- 创建索引
    INDEX `idx_user_name` (`User_name`),
    INDEX `idx_telephone` (`telephone`),
    INDEX `idx_location` (`User_Loc_longitude`, `User_Loc_latitude`),
    INDEX `idx_create_time` (`Create_at`),
    INDEX `idx_user_status` (`User_sta`),

    -- 添加唯一约束
    UNIQUE KEY `uk_user_name` (`User_name`),
    UNIQUE KEY `uk_telephone` (`telephone`),
    UNIQUE KEY `uk_id_card` (`ID`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 6. 创建商品类别表
CREATE TABLE IF NOT EXISTS `categories` (
    `category_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '商品类别ID，主键',
    `category` VARCHAR(64) NOT NULL COMMENT '存储商品具体类别',

    -- 创建索引
    INDEX `idx_category_name` (`category`),

    -- 添加唯一约束
    UNIQUE KEY `uk_category_name` (`category`)

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
    `main_image_url` VARCHAR(2048) DEFAULT NULL COMMENT '商品主图链接',
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
    CONSTRAINT `chk_price_positive` CHECK (`current_price` > 0),
    CONSTRAINT `chk_quantity_non_negative` CHECK (`quantity` >= 0)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 8. 插入基础商品类别数据
INSERT INTO `categories` (`category`) VALUES
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
ON DUPLICATE KEY UPDATE `category` = VALUES(`category`);

-- 9. 创建订单表
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

-- 10. 创建钱包表
CREATE TABLE IF NOT EXISTS `wallet` (
    `User_ID` CHAR(9) NOT NULL PRIMARY KEY COMMENT '主键/外键，用户ID',
    `money` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '用户余额（默认为0）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 保留的索引
    INDEX `idx_money` (`money`),
    INDEX `idx_updated_at` (`updated_at`),

    -- 外键约束
    CONSTRAINT `fk_wallet_user` FOREIGN KEY (`User_ID`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE,

    -- 保留的检查约束
    CONSTRAINT `chk_money_positive` CHECK (`money` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包表';

-- 11. 聊天会话表
CREATE TABLE IF NOT EXISTS `chat_sessions` (
    `session_id` CHAR(36) NOT NULL PRIMARY KEY COMMENT '会话ID（UUID）',
    `buyer_id` CHAR(9) NOT NULL COMMENT '买家ID',
    `seller_id` CHAR(9) NOT NULL COMMENT '卖家ID',
    `session_status` ENUM('active', 'closed') NOT NULL DEFAULT 'active' COMMENT '会话状态',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 索引
    INDEX `idx_buyer_id` (`buyer_id`),
    INDEX `idx_seller_id` (`seller_id`),
    INDEX `idx_session_status` (`session_status`),
    INDEX `idx_created_at` (`created_at`),


    -- 外键约束
    CONSTRAINT `fk_chat_sessions_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE,
    CONSTRAINT `fk_chat_sessions_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE,


    -- 唯一约束：同一买家和卖家只能有一个会话
    UNIQUE KEY `uk_buyer_seller_commodity` (`buyer_id`, `seller_id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

-- 12. 聊天消息表
CREATE TABLE IF NOT EXISTS `chat_messages` (
    `message_id` CHAR(36) NOT NULL PRIMARY KEY COMMENT '消息ID（UUID）',
    `session_id` CHAR(36) NOT NULL COMMENT '会话ID',
    `sender_id` CHAR(9) NOT NULL COMMENT '发送者ID',
    `receiver_id` CHAR(9) NOT NULL COMMENT '接收者ID',
    `message_type` ENUM('text', 'image', 'file') NOT NULL DEFAULT 'text' COMMENT '消息类型',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `file_url` VARCHAR(500) DEFAULT NULL COMMENT '文件URL（图片/文件消息）',
    `read_status` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已读',
    `sent_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `read_at` DATETIME DEFAULT NULL COMMENT '阅读时间',

    -- 索引
    INDEX `idx_session_id` (`session_id`),
    INDEX `idx_sender_id` (`sender_id`),
    INDEX `idx_receiver_id` (`receiver_id`),
    INDEX `idx_message_type` (`message_type`),
    INDEX `idx_read_status` (`read_status`),
    INDEX `idx_sent_at` (`sent_at`),

    -- 复合索引
    INDEX `idx_session_time` (`session_id`, `sent_at`),
    INDEX `idx_receiver_read` (`receiver_id`, `read_status`),

    -- 外键约束
    CONSTRAINT `fk_chat_messages_session` FOREIGN KEY (`session_id`) REFERENCES `chat_sessions` (`session_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_chat_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE,
    CONSTRAINT `fk_chat_messages_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- 13. 创建购物车表
CREATE TABLE IF NOT EXISTS `cart` (
    `Cart_ID` VARCHAR(10) NOT NULL PRIMARY KEY COMMENT '主键',
    `User_ID` CHAR(9) NOT NULL COMMENT '外键指向users表',
    `commodity_id` CHAR(36) NOT NULL COMMENT '商品唯一标识符（UUIDv7）//外键指向commodities表',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '购物车添加时间',

    -- 创建索引
    INDEX `idx_cart_user_id` (`User_ID`),
    INDEX `idx_cart_commodity_id` (`commodity_id`),
    INDEX `idx_cart_created_at` (`created_at`),

    -- 外键约束
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`User_ID`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_cart_commodity` FOREIGN KEY (`commodity_id`) REFERENCES `commodities` (`commodity_id`) ON DELETE CASCADE ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 14. 创建管理员表
CREATE TABLE IF NOT EXISTS `Root` (
    `Root_id` CHAR(9) NOT NULL PRIMARY KEY COMMENT '主键',
    `password` VARCHAR(64) NOT NULL COMMENT 'sha256'

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 15. 创建公告表
CREATE TABLE IF NOT EXISTS `announcements` (
    `Announcement_Id` VARCHAR(10) NOT NULL PRIMARY KEY COMMENT '公告ID，主键',
    `Root_id` CHAR(9) NOT NULL COMMENT '外键指向Root表',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '公告创建时间',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `visible_status` BOOLEAN NOT NULL DEFAULT 0 COMMENT '公告是否可见，0为可见，1为不可见，默认值为0',

    -- 创建索引
    INDEX `idx_announcement_root_id` (`Root_id`),
    INDEX `idx_announcement_created_at` (`created_at`),
    INDEX `idx_announcement_visible_status` (`visible_status`),

    -- 外键约束
    CONSTRAINT `fk_announcements_root` FOREIGN KEY (`Root_id`) REFERENCES `Root` (`Root_id`) ON DELETE CASCADE ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- 16. 创建申诉表
CREATE TABLE IF NOT EXISTS `appeals` (
    `Argument_Id` VARCHAR(10) NOT NULL PRIMARY KEY COMMENT '申诉ID，主键',
    `Argue1_id` CHAR(9) NOT NULL COMMENT '外键指向Root表的User_ID，申诉发起者',
    `Argue2_id` CHAR(9) NOT NULL COMMENT '外键指向Root表的User_ID，被申诉者',
    `order_id` CHAR(36) NOT NULL COMMENT 'UUID v7，作为外键指向orders表',
    `Reason` TEXT NOT NULL COMMENT '申诉理由',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申诉发起（创建）时间',
    `Root_id` CHAR(9) DEFAULT NULL COMMENT '外键指向Root表',
    `status` BOOLEAN DEFAULT FALSE comment '是否通过，通过代表完成'

    -- 创建索引
    INDEX `idx_appeal_argue1_id` (`Argue1_id`),
    INDEX `idx_appeal_argue2_id` (`Argue2_id`),
    INDEX `idx_appeal_order_id` (`order_id`),
    INDEX `idx_appeal_created_at` (`created_at`),
    INDEX `idx_appeal_root_id` (`Root_id`),

    -- 外键约束
    CONSTRAINT `fk_appeals_argue1` FOREIGN KEY (`Argue1_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_appeals_argue2` FOREIGN KEY (`Argue2_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_appeals_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_appeals_root` FOREIGN KEY (`Root_id`) REFERENCES `Root` (`Root_id`) ON DELETE SET NULL ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='申诉表';

-- 17. 创建特征表
CREATE TABLE IF NOT EXISTS `tags` (
    `TID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `tags_Id` JSON DEFAULT NULL COMMENT '标签ID（JSON格式）',
    `category_id` INT UNSIGNED NOT NULL COMMENT '外键指向categories',
    `tag_Name` VARCHAR(20) NOT NULL COMMENT '特征名',

    -- 创建索引
    INDEX `idx_tags_category_id` (`category_id`),
    INDEX `idx_tags_tag_name` (`tag_Name`),

    -- 外键约束
    CONSTRAINT `fk_tags_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='特征表';

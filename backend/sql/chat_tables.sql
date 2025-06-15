-- 聊天会话表
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
    INDEX `idx_commodity_id` (`commodity_id`),
    INDEX `idx_session_status` (`session_status`),
    INDEX `idx_created_at` (`created_at`),
    
    -- 复合索引
    INDEX `idx_buyer_commodity` (`buyer_id`, `commodity_id`),
    INDEX `idx_seller_commodity` (`seller_id`, `commodity_id`),
    
    -- 外键约束
    CONSTRAINT `fk_chat_sessions_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE,
    CONSTRAINT `fk_chat_sessions_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE,
    CONSTRAINT `fk_chat_sessions_commodity` FOREIGN KEY (`commodity_id`) REFERENCES `commodities` (`commodity_id`) ON DELETE CASCADE,
    
    -- 唯一约束：同一商品的买家和卖家只能有一个会话
    UNIQUE KEY `uk_buyer_seller_commodity` (`buyer_id`, `seller_id`, `commodity_id`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

-- 聊天消息表
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
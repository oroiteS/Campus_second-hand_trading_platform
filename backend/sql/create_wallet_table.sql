-- 创建校园二手交易平台钱包表
-- 基于钱包设计文档中的数据库表结构

USE campus;

CREATE TABLE IF NOT EXISTS `wallet` (
    `User_ID` CHAR(9) NOT NULL PRIMARY KEY COMMENT '主键/外键，用户ID',
    `pending_income` DECIMAL(10,2) DEFAULT 0.00 COMMENT '待转入金额（默认为0）',
    `money` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '用户余额（默认为0）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 创建索引
    INDEX `idx_money` (`money`),
    INDEX `idx_pending_income` (`pending_income`),
    INDEX `idx_updated_at` (`updated_at`),
    
    -- 外键约束
    CONSTRAINT `fk_wallet_user` FOREIGN KEY (`User_ID`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- 检查约束（确保金额不为负数）
    CONSTRAINT `chk_money_positive` CHECK (`money` >= 0),
    CONSTRAINT `chk_pending_income_positive` CHECK (`pending_income` >= 0)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包表';

-- 创建钱包交易记录表（用于记录钱包操作历史）
CREATE TABLE IF NOT EXISTS `wallet_transactions` (
    `transaction_id` CHAR(36) NOT NULL PRIMARY KEY COMMENT '交易记录ID',
    `user_id` CHAR(9) NOT NULL COMMENT '用户ID',
    `transaction_type` ENUM('deposit', 'withdraw', 'transfer_in', 'transfer_out', 'freeze', 'unfreeze', 'refund') NOT NULL COMMENT '交易类型',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '交易金额',
    `balance_before` DECIMAL(10,2) NOT NULL COMMENT '交易前余额',
    `balance_after` DECIMAL(10,2) NOT NULL COMMENT '交易后余额',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '交易描述',
    `related_order_id` CHAR(36) DEFAULT NULL COMMENT '关联订单ID（如果有）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    
    -- 创建索引
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_transaction_type` (`transaction_type`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_user_time` (`user_id`, `created_at`),
    INDEX `idx_related_order` (`related_order_id`),
    
    -- 外键约束
    CONSTRAINT `fk_wallet_trans_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`User_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_wallet_trans_order` FOREIGN KEY (`related_order_id`) REFERENCES `orders` (`order_id`) ON DELETE SET NULL ON UPDATE CASCADE
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包交易记录表';

-- 显示表结构
DESCRIBE wallet;
DESCRIBE wallet_transactions;

-- 验证表结构
SELECT 
    TABLE_NAME as '表名',
    COLUMN_NAME as '字段名',
    DATA_TYPE as '类型',
    IS_NULLABLE as '是否可为空',
    COLUMN_DEFAULT as '默认值',
    COLUMN_COMMENT as '描述'
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'campus' AND TABLE_NAME IN ('wallet', 'wallet_transactions')
ORDER BY TABLE_NAME, ORDINAL_POSITION;
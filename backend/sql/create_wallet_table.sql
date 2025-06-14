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

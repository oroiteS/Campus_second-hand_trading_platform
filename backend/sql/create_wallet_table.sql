-- 创建校园二手交易平台钱包表
-- 基于钱包设计文档中的数据库表结构

USE campus;

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


-- 显示表结构
DESCRIBE wallet;

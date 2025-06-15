-- 创建校园二手交易平台用户表
-- 基于需求分析文档中的用户表结构

USE campus;

CREATE TABLE IF NOT EXISTS `users` (
    `User_ID` CHAR(9) NOT NULL PRIMARY KEY COMMENT '主键，用户ID',
    `User_name` VARCHAR(20) NOT NULL COMMENT '存储用户名',
    `password` VARCHAR(64) NOT NULL COMMENT 'sha256加密后的密码',
    `telephone` CHAR(11) NOT NULL COMMENT '用户电话号',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像url（默认地址：）',
    `User_Loc_longitude` DECIMAL(9,6) NOT NULL COMMENT '存储经度',
    `User_sta` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否封号（默认否）',
    `Create_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `ID` CHAR(18) NOT NULL COMMENT '身份证号',
    `User_Loc_latitude` DECIMAL(9,6) NOT NULL COMMENT '存储纬度',
    
    -- 创建索引
    INDEX `idx_user_name` (`User_name`),
    INDEX `idx_telephone` (`telephone`),
    INDEX `idx_location` (`User_Loc_longitude`, `User_Loc_latitude`),
    
    -- 添加唯一约束
    UNIQUE KEY `uk_user_name` (`User_name`),
    UNIQUE KEY `uk_telephone` (`telephone`),
    UNIQUE KEY `uk_id_card` (`ID`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建空间索引（用于地理位置查询优化）
-- ALTER TABLE users ADD SPATIAL INDEX `spatial_location` (POINT(User_Loc_longitude, User_Loc_latitude));

-- 插入示例数据（可选）
/*
INSERT INTO users (
    User_ID, User_name, password, telephone, real_name, 
    User_Loc_longitude, User_Loc_latitude, ID
) VALUES 
(
    '202100001', '高铭轩', 
    SHA2('password123', 256), 
    '13800138001', 
    '高铭轩',
    121.506377, 31.245417,
    '310101199901010001'
),
(
    '202100002', 'Harry', 
    SHA2('password456', 256), 
    '13800138002', 
    '哈里·波特',
    121.506377, 31.245417,
    '310101199902020002'
),
(
    '202100003', '杨裕凯', 
    SHA2('password789', 256), 
    '13800138003', 
    '杨裕凯',
    121.506377, 31.245417,
    '310101199903030003'
),
(
    '202100004', '李韦晨', 
    SHA2('passwordabc', 256), 
    '13800138004', 
    '李韦晨',
    121.506377, 31.245417,
    '310101199904040004'
);
*/
CREATE database campus;
use campus;
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '自增ID',
    `userid` VARCHAR(15) NOT NULL UNIQUE COMMENT '用户ID',
    `username` VARCHAR(20) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '哈希加密后的密码',
    `role` ENUM('user', 'admin') NOT NULL DEFAULT 'user' COMMENT '角色: user, admin',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
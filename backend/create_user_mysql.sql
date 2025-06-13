-- 创建用户并设置密码
CREATE USER 'campus_test'@'%' IDENTIFIED BY 'campus_suep';

-- 授予用户在campus数据库上的所有权限
GRANT ALL PRIVILEGES ON campus.* TO 'campus_test'@'%';

-- 刷新权限使更改生效
FLUSH PRIVILEGES;
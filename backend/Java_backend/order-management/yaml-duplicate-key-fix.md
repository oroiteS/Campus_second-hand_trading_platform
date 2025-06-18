# YAML配置重复键修复指南

## 问题描述

应用启动时出现 `DuplicateKeyException` 错误：
```
org.yaml.snakeyaml.constructor.DuplicateKeyException: while constructing a mapping
found duplicate key springdoc
```

## 问题原因

在 `application.yml` 文件中存在重复的配置键，具体是：
- `springdoc` 配置在文件中出现了两次
- `server` 配置也存在重复
- `logging` 配置存在重复

## 已修复的问题

### 1. 移除重复的 springdoc 配置
- **位置1**：第89行附近的 springdoc 配置（已移除）
- **位置2**：第130行附近的 springdoc 配置（保留并增强）

### 2. 合并 server 配置
- 保留主要的 server 配置（端口8081，上下文路径等）
- 注释掉重复的 base-url 配置

### 3. 合并 logging 配置
- 将所有日志级别配置合并到一个 logging 块中
- 移除重复的日志配置

## 最终的 Swagger 配置

```yaml
# Swagger 配置
springdoc:
  api-docs:
    # 启用 API 文档
    enabled: true
    # API 文档路径
    path: /v3/api-docs
  swagger-ui:
    # 启用 Swagger UI
    enabled: true
    # Swagger UI 路径
    path: /swagger-ui.html
    # 操作排序
    operations-sorter: alpha
    # 标签排序
    tags-sorter: alpha
    # 尝试请求
    try-it-out-enabled: true
  show-actuator: true
```

## 验证修复

### 方法1：使用Maven启动
```bash
cd order-management
mvn clean compile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 方法2：使用批处理脚本
```bash
# 运行项目根目录下的启动脚本
start-swagger-test.bat
```

### 方法3：在IDE中启动
1. 在IDE中打开 `OrderManagementApplication.java`
2. 设置VM参数：`-Dspring.profiles.active=dev`
3. 运行主方法

## 访问地址

修复成功后，可以访问以下地址：

- **Swagger UI**: http://localhost:8081/order-management/swagger-ui.html
- **API文档**: http://localhost:8081/order-management/v3/api-docs
- **健康检查**: http://localhost:8081/order-management/actuator/health

## 常见问题排查

### 1. 如果仍然出现重复键错误
- 检查是否还有其他重复的配置键
- 使用YAML验证工具检查语法
- 确保缩进使用空格而不是制表符

### 2. 如果应用无法启动
- 检查数据库连接配置
- 确保MySQL服务正在运行
- 检查端口8081是否被占用

### 3. 如果Swagger UI无法访问
- 确认应用已成功启动
- 检查防火墙设置
- 验证上下文路径配置

## 预防措施

1. **使用YAML验证工具**：在修改配置文件前使用在线YAML验证器
2. **IDE插件**：安装YAML语法检查插件
3. **配置管理**：将不同环境的配置分离到不同的profile文件中
4. **代码审查**：配置文件修改时进行代码审查

## 下一步操作

1. 重新启动应用
2. 访问Swagger UI验证功能
3. 运行API测试确保所有接口正常工作
4. 检查日志输出确认配置生效

---

**注意**：如果问题仍然存在，请检查是否有其他配置文件（如 `application-dev.yml`）中也存在重复配置。
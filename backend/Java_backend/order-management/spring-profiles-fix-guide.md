# Spring Boot Profile 配置修复指南

## 问题描述

应用启动时出现以下错误：
```
org.springframework.boot.context.config.InvalidConfigDataPropertyException: 
Property 'spring.profiles.active' imported from location 'class path resource [application-dev.yml]' 
is invalid in a profile specific resource
```

## 问题原因

在Spring Boot中，**不能在profile特定的配置文件中设置 `spring.profiles.active` 属性**。这是Spring Boot的设计限制，因为：

1. Profile特定的配置文件（如 `application-dev.yml`）本身就是为特定profile服务的
2. 在这些文件中再次指定 `spring.profiles.active` 会造成循环依赖和配置冲突
3. Profile的激活应该在主配置文件或启动参数中指定

## 修复方案

### 1. 移除profile特定文件中的配置

**修复前** (`application-dev.yml`)：
```yaml
spring:
  profiles:
    active: dev  # ❌ 错误：不能在profile特定文件中设置
  datasource:
    # 其他配置...
```

**修复后** (`application-dev.yml`)：
```yaml
spring:
  # 数据源配置
  datasource:
    # 其他配置...
```

### 2. 正确的Profile激活方式

#### 方式1：在主配置文件中设置（推荐）
在 `application.yml` 中：
```yaml
spring:
  profiles:
    active: dev
```

#### 方式2：通过启动参数
```bash
# Maven启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Java启动
java -jar app.jar --spring.profiles.active=dev

# 环境变量
export SPRING_PROFILES_ACTIVE=dev
```

#### 方式3：在IDE中设置
- **IDEA**: Run Configuration → VM options: `-Dspring.profiles.active=dev`
- **Eclipse**: Run Configuration → Arguments → VM arguments: `-Dspring.profiles.active=dev`

## Spring Boot配置文件最佳实践

### 1. 配置文件层次结构

```
src/main/resources/
├── application.yml              # 主配置文件（通用配置）
├── application-dev.yml          # 开发环境配置
├── application-test.yml         # 测试环境配置
├── application-prod.yml         # 生产环境配置
└── application-local.yml        # 本地开发配置（可选）
```

### 2. 配置文件内容分工

#### `application.yml` - 主配置文件
```yaml
# 应用基本信息
spring:
  application:
    name: order-management
  profiles:
    active: dev  # 默认激活的profile

# 通用配置（所有环境共享）
server:
  servlet:
    encoding:
      charset: UTF-8
      enabled: true

# MyBatis Plus通用配置
mybatis-plus:
  type-aliases-package: com.campus.ordermanagement.pojo
  global-config:
    db-config:
      id-type: INPUT
      logic-delete-field: deleted
```

#### `application-dev.yml` - 开发环境配置
```yaml
# 开发环境特定配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_trading_dev
    username: dev_user
    password: dev_password
  
  # 开发工具
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

# 开发环境日志级别
logging:
  level:
    com.campus.ordermanagement: DEBUG
    org.springframework.web: DEBUG

# Swagger配置（仅开发环境）
springdoc:
  swagger-ui:
    enabled: true
```

#### `application-prod.yml` - 生产环境配置
```yaml
# 生产环境配置
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10

# 生产环境日志
logging:
  level:
    com.campus.ordermanagement: INFO
    org.springframework.web: WARN
  file:
    name: /var/log/order-management.log

# 禁用Swagger（生产环境）
springdoc:
  swagger-ui:
    enabled: false
```

### 3. 环境变量管理

#### 开发环境 `.env` 文件（可选）
```bash
# 数据库配置
DB_URL=jdbc:mysql://localhost:3306/campus_trading_dev
DB_USERNAME=dev_user
DB_PASSWORD=dev_password

# 应用配置
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8081
```

#### 生产环境变量
```bash
# 必须通过环境变量设置的敏感信息
export DB_URL="jdbc:mysql://prod-db:3306/campus_trading"
export DB_USERNAME="prod_user"
export DB_PASSWORD="secure_password"
export SPRING_PROFILES_ACTIVE="prod"
```

### 4. 条件配置

#### 使用 `@Profile` 注解
```java
@Configuration
@Profile("dev")
public class DevConfig {
    
    @Bean
    public TestDataInitializer testDataInitializer() {
        return new TestDataInitializer();
    }
}

@Configuration
@Profile("prod")
public class ProdConfig {
    
    @Bean
    public SecurityConfig securityConfig() {
        return new SecurityConfig();
    }
}
```

#### 配置文件中的条件配置
```yaml
# 在主配置文件中使用条件配置
spring:
  config:
    activate:
      on-profile: dev
  # 开发环境特定配置
---
spring:
  config:
    activate:
      on-profile: prod
  # 生产环境特定配置
```

### 5. 配置验证

#### 启动时验证配置
```java
@Component
@ConfigurationProperties(prefix = "order.config")
@Validated
public class OrderConfigProperties {
    
    @NotNull
    @Min(1)
    private Integer timeoutMinutes;
    
    @NotNull
    @Max(1000)
    private Integer maxOrdersPerUser;
    
    // getters and setters
}
```

### 6. 多Profile激活

```yaml
# 同时激活多个profile
spring:
  profiles:
    active: dev,swagger,debug
```

```bash
# 命令行激活多个profile
java -jar app.jar --spring.profiles.active=dev,swagger
```

## 常见错误和解决方案

### 1. Profile文件命名错误
```
❌ application_dev.yml    # 错误：使用下划线
✅ application-dev.yml    # 正确：使用连字符
```

### 2. 配置重复定义
```yaml
# ❌ 错误：在多个文件中重复定义相同配置
# application.yml
server:
  port: 8080

# application-dev.yml  
server:
  port: 8081  # 这会覆盖主配置文件中的设置
```

### 3. 敏感信息硬编码
```yaml
# ❌ 错误：直接写在配置文件中
spring:
  datasource:
    password: mypassword123

# ✅ 正确：使用环境变量
spring:
  datasource:
    password: ${DB_PASSWORD:defaultpassword}
```

## 验证修复

### 1. 检查配置加载
```bash
# 启动应用并查看日志
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 查看激活的profile
curl http://localhost:8081/order-management/actuator/env
```

### 2. 配置文件语法验证
```bash
# 使用在线YAML验证器
# 或者使用命令行工具
yamllint application-dev.yml
```

### 3. 应用健康检查
```bash
# 检查应用状态
curl http://localhost:8081/order-management/actuator/health

# 检查配置信息
curl http://localhost:8081/order-management/actuator/configprops
```

## 总结

1. **永远不要在profile特定的配置文件中设置 `spring.profiles.active`**
2. **使用主配置文件或启动参数来激活profile**
3. **合理分离不同环境的配置**
4. **使用环境变量管理敏感信息**
5. **遵循Spring Boot配置文件命名约定**
6. **定期验证配置文件语法和逻辑**

通过遵循这些最佳实践，可以避免配置相关的错误，提高应用的可维护性和部署的灵活性。
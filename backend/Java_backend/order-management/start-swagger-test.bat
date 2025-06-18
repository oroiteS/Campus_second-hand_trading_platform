@echo off
chcp 65001
echo ====================================
echo 校园二手交易平台 - 订单管理模块
echo Swagger UI 测试环境启动脚本
echo ====================================
echo.

echo [1/4] 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误：未找到Java环境，请确保已安装Java 17+
    pause
    exit /b 1
)

echo [2/4] 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误：未找到Maven环境，请确保已安装Maven 3.6+
    pause
    exit /b 1
)

echo [3/4] 编译项目...
mvn clean compile
if %errorlevel% neq 0 (
    echo 错误：项目编译失败，请检查代码和依赖
    pause
    exit /b 1
)

echo [4/4] 启动服务...
echo.
echo 正在启动订单管理服务...
echo 请等待服务启动完成...
echo.
echo 服务启动后可以通过以下地址访问：
echo - Swagger UI: http://localhost:8081/order-management/swagger-ui.html
echo - API文档: http://localhost:8081/order-management/api-docs
echo - 健康检查: http://localhost:8081/order-management/actuator/health
echo.
echo 按 Ctrl+C 停止服务
echo ====================================
echo.

:: 启动Spring Boot应用
mvn spring-boot:run -Dspring-boot.run.profiles=dev

echo.
echo 服务已停止
pause
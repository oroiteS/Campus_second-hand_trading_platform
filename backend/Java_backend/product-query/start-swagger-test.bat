@echo off
chcp 65001 >nul
echo ========================================
echo   商品查询服务 Swagger UI 测试启动脚本
echo ========================================
echo.
echo 正在启动商品查询服务...
echo.

REM 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到Java环境，请确保已安装Java 17或更高版本
    pause
    exit /b 1
)

REM 检查Maven环境
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到Maven环境，请确保已安装Maven
    pause
    exit /b 1
)

echo 环境检查通过，正在启动服务...
echo.
echo 请等待服务启动完成，然后访问以下地址：
echo.
echo - Swagger UI: http://localhost:8082/product-query/swagger-ui.html
echo - API文档: http://localhost:8082/product-query/v3/api-docs
echo.
echo 按 Ctrl+C 停止服务
echo ========================================
echo.

REM 启动Spring Boot应用
mvn spring-boot:run

echo.
echo 服务已停止
pause
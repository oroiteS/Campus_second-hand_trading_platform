@echo off
chcp 65001
echo ====================================
echo 校园二手交易平台 - 订单管理模块
echo 清理和重新编译脚本
echo ====================================
echo.

echo [1/6] 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误：未找到Java环境，请确保已安装Java 17+
    pause
    exit /b 1
)

echo [2/6] 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误：未找到Maven环境，请确保已安装Maven 3.8+
    pause
    exit /b 1
)

echo [3/6] 清理项目缓存...
echo 正在清理target目录...
if exist target rmdir /s /q target
echo 正在清理IDE缓存...
if exist .idea rmdir /s /q .idea
if exist *.iml del /q *.iml
echo 清理完成

echo [4/6] 清理Maven缓存...
mvn clean
if %errorlevel% neq 0 (
    echo 警告：Maven clean执行失败，继续尝试编译...
)

echo [5/6] 强制更新依赖...
echo 正在下载最新依赖...
mvn dependency:resolve -U
if %errorlevel% neq 0 (
    echo 警告：依赖更新失败，继续尝试编译...
)

echo [6/6] 重新编译项目...
echo 正在编译项目...
mvn compile
if %errorlevel% neq 0 (
    echo.
    echo ❌ 编译失败！
    echo.
    echo 可能的解决方案：
    echo 1. 检查Java版本是否为17+
    echo 2. 检查IDE是否正确配置Lombok插件
    echo 3. 尝试重启IDE后再次运行此脚本
    echo 4. 查看详细错误信息并参考fix-lombok-java17.md
    echo.
    pause
    exit /b 1
)

echo.
echo ✅ 编译成功！
echo.
echo 现在可以启动服务：
echo mvn spring-boot:run -Dspring-boot.run.profiles=dev
echo.
echo 或者运行完整构建：
echo mvn clean install
echo.
echo ====================================
pause
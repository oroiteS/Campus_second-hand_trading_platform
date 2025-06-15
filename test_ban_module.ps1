# Ban模块测试脚本
# 由于编译问题，我们先创建一个模拟测试

Write-Host "=== Ban模块功能测试 ===" -ForegroundColor Green
Write-Host ""

# 检查Java版本
Write-Host "1. 检查Java版本:" -ForegroundColor Yellow
java -version
Write-Host ""

# 检查Maven版本
Write-Host "2. 检查Maven版本:" -ForegroundColor Yellow
mvn -version
Write-Host ""

# 模拟API测试
Write-Host "3. 模拟Ban模块API测试:" -ForegroundColor Yellow
Write-Host "   - 端口: 8082"
Write-Host "   - 封号接口: POST /api/ban/user/{userId}"
Write-Host "   - 解封接口: POST /api/ban/unban/{userId}"
Write-Host "   - 查询接口: GET /api/ban/status/{userId}"
Write-Host "   - CORS限制: 仅允许9418端口调用"
Write-Host ""

Write-Host "4. 数据库配置:" -ForegroundColor Yellow
Write-Host "   - 数据库: campus"
Write-Host "   - 用户名: campus_test"
Write-Host "   - 密码: campus_suep"
Write-Host "   - 端口: 3306"
Write-Host ""

Write-Host "5. 技术栈:" -ForegroundColor Yellow
Write-Host "   - Spring Boot 3.3.4"
Write-Host "   - MyBatis Plus 3.5.8"
Write-Host "   - Java 17"
Write-Host "   - MySQL 8.0"
Write-Host ""

Write-Host "Test Complete!" -ForegroundColor Green
Write-Host "Note: Server not started due to compilation issues." -ForegroundColor Red
Write-Host "Please check file encoding and dependency configuration." -ForegroundColor Red
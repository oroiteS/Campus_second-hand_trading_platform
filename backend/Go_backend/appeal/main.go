package main

import (
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/config"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/database"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/handlers"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/services"
	"log"
	"time"
)

// @title 申诉服务 API
// @version 1.0
// @description 校园二手交易平台申诉模块API文档
// @termsOfService http://swagger.io/terms/

// @contact.name API Support
// @contact.url http://www.swagger.io/support
// @contact.email support@swagger.io

// @license.name Apache 2.0
// @license.url http://www.apache.org/licenses/LICENSE-2.0.html

// @host localhost:8080
// @BasePath /api/v1
// @schemes http https
func main() {
	// 加载配置
	cfg := config.Load()

	// 初始化数据库
	db, err := database.InitMySQL(cfg.MySQL)
	if err != nil {
		log.Fatalf("数据库连接失败: %v", err)
	}

	// 初始化服务
	appealService := services.NewAppealService(db)

	// 初始化处理器
	appealHandler := handlers.NewAppealHandler(appealService)

	// 初始化Gin路由
	r := gin.Default()

	// 使用官方CORS中间件
	r.Use(cors.New(cors.Config{
		AllowOrigins:     []string{"*"},
		AllowMethods:     []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowHeaders:     []string{"Origin", "Content-Type", "Authorization", "User-ID"},
		ExposeHeaders:    []string{"Content-Length"},
		AllowCredentials: true,
		MaxAge:           12 * time.Hour,
	}))

	// 设置路由
	api := r.Group("/api-8093")
	{
		appeals := api.Group("/appeals")
		{
			// 通过订单ID查看申诉状态
			appeals.GET("/order/:orderId", appealHandler.GetAppealStatusByOrderId)
			// 批量查询申诉状态
			appeals.POST("/batch-status", appealHandler.BatchGetAppealStatusByOrderIds)
			// 发起申诉请求
			appeals.POST("", appealHandler.CreateAppeal)
			// 取消申诉
			appeals.PUT("/:argumentId/cancel", appealHandler.CancelAppeal)
			// 管理员更新申诉状态
			appeals.PUT("/:argumentId/admin-update", appealHandler.AdminUpdateAppealStatus)
			// 获取所有申诉记录
			appeals.GET("/all", appealHandler.GetAllAppeals)
			// 批量查询申诉记录详情
			appeals.POST("/batch-appeals", appealHandler.BatchGetAppeals)
		}
	}

	// 启动服务器
	log.Printf("申诉服务启动在端口 %s", cfg.Server.Port)
	if err := r.Run(":" + cfg.Server.Port); err != nil {
		log.Fatalf("服务器启动失败: %v", err)
	}
}

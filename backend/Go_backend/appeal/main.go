package main

import (
	"github.com/gin-gonic/gin"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/config"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/database"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/handlers"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/services"
	"log"
)

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

	// 设置路由
	api := r.Group("/api/v1")
	{
		appeals := api.Group("/appeals")
		{
			// 通过订单ID查看申诉状态 - 修复路由
			appeals.GET("/order/:orderId", appealHandler.GetAppealStatusByOrderId)
			// 批量查询申诉状态
			appeals.POST("/batch-status", appealHandler.BatchGetAppealStatusByOrderIds)
			// 发起申诉请求
			appeals.POST("", appealHandler.CreateAppeal) // 移除末尾的斜杠
			// 取消申诉
			appeals.PUT("/:argumentId/cancel", appealHandler.CancelAppeal)
			// 管理员更新申诉状态
			appeals.PUT("/:argumentId/admin-update", appealHandler.AdminUpdateAppealStatus)
		}
	}

	// 启动服务器
	log.Printf("申诉服务启动在端口 %s", cfg.Server.Port)
	if err := r.Run(":" + cfg.Server.Port); err != nil {
		log.Fatalf("服务器启动失败: %v", err)
	}
}

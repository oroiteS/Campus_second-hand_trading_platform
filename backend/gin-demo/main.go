package main

import (
	"log"

	"github.com/oroiteS/Campus_second-hand_trading_platform/config"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/models"
	"github.com/oroiteS/Campus_second-hand_trading_platform/pkg/database"
	"github.com/oroiteS/Campus_second-hand_trading_platform/routes"
)

func main() {
	// 1. 加载配置
	config.LoadConfig()

	// 2. 初始化数据库连接
	database.InitMySQL(config.AppConfig)

	// 3. 自动迁移数据库表结构 (仅在开发环境或首次运行时执行)
	// 在生产环境中，通常使用专门的迁移工具进行数据库结构变更管理
	err := database.DB.AutoMigrate(&models.User{})
	if err != nil {
		log.Fatalf("Failed to migrate database: %v", err)
	}
	log.Println("Database migration completed successfully.")

	// 4. 设置路由
	r := routes.SetupRouter()

	// 5. 启动服务
	log.Println("Starting server on :8080")
	if err := r.Run(":8080"); err != nil {
		log.Fatalf("Failed to run server: %v", err)
	}
}

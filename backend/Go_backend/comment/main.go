package main

import (
	"log"

	"github.com/gin-gonic/gin"

	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/comment/config"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/comment/database"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/comment/handlers"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/comment/models"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/comment/routes"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/comment/services"
)

// @title Campus Comment API
// @version 1.0
// @description 校园二手交易平台评论API
// @termsOfService http://swagger.io/terms/

// @contact.name API Support
// @contact.url http://www.swagger.io/support
// @contact.email support@swagger.io

// @license.name Apache 2.0
// @license.url http://www.apache.org/licenses/LICENSE-2.0.html

// @host localhost:8088
// @BasePath /api/v1
func main() {
	// 加载配置
	cfg := config.Load()

	// 初始化数据库连接
	db, err := database.InitMySQL(&cfg.MySQL)
	if err != nil {
		log.Fatalf("初始化MySQL失败: %v", err)
	}

	// 初始化Redis连接
	rdb, err := database.InitRedis(cfg.Redis)
	if err != nil {
		log.Fatalf("初始化Redis失败: %v", err)
	}

	/*
	// 自动迁移数据库表
	err = db.AutoMigrate(&models.Comment{})
	if err != nil {
		log.Fatalf("数据库迁移失败: %v", err)
	}*/

	// 初始化服务
	commentService := services.NewCommentService(db, rdb)

	// 初始化处理器
	commentHandler := handlers.NewCommentHandler(commentService)

	// 初始化Gin引擎
	r := gin.Default()

	// 添加CORS中间件
	r.Use(func(c *gin.Context) {
		c.Header("Access-Control-Allow-Origin", "*")
		c.Header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
		c.Header("Access-Control-Allow-Headers", "Content-Type, Authorization")
		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}
		c.Next()
	})

	// 设置路由
	routes.SetupRoutes(r, commentHandler)

	// 健康检查
	r.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{"status": "ok", "service": "comment-service"})
	})

	// 启动服务器
	log.Printf("评论服务启动在端口: %s", cfg.Server.Port)
	if err := r.Run(":" + cfg.Server.Port); err != nil {
		log.Fatalf("启动服务器失败: %v", err)
	}
}

package main

import (
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"

	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/chat/config"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/chat/database"
	_ "github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/chat/docs"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/chat/handlers"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/chat/middleware"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/chat/services"
)

// @title Campus Chat API
// @version 1.0
// @description 校园二手交易平台聊天系统API
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
	db, err := database.InitMySQL(cfg.MySQL)
	if err != nil {
		log.Fatal("Failed to connect to MySQL:", err)
	}

	// 初始化Redis连接
	rdb, err := database.InitRedis(cfg.Redis)
	if err != nil {
		log.Fatal("Failed to connect to Redis:", err)
	}

	// 初始化服务
	chatService := services.NewChatService(db, rdb)
	webSocketHub := services.NewWebSocketHub(chatService)
	go webSocketHub.Run()

	// 初始化处理器
	chatHandler := handlers.NewChatHandler(chatService, webSocketHub)

	// 设置Gin模式
	gin.SetMode(gin.ReleaseMode)
	r := gin.Default()

	// 添加CORS中间件
	r.Use(middleware.CORS())

	// API路由组
	v1 := r.Group("/api/v1")
	{
		// WebSocket连接
		v1.GET("/ws/:user_id", chatHandler.HandleWebSocket)

		// 聊天相关API
		chat := v1.Group("/chat")
		{
			chat.POST("/sessions", chatHandler.CreateSession)
			chat.GET("/user/:user_id/sessions", chatHandler.GetUserSessions)
			chat.GET("/session/:session_id/messages", chatHandler.GetSessionMessages)
			chat.POST("/messages", chatHandler.SendMessage)
			chat.PUT("/messages/:message_id/read", chatHandler.MarkAsRead)
			chat.GET("/user/:user_id/unread", chatHandler.GetUnreadCount)
			// 添加用户信息获取路由
			chat.GET("/user/:user_id/info", chatHandler.GetCurrentUser)
		}
	}

	// Swagger文档
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	// 健康检查
	r.GET("/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{"status": "ok"})
	})

	log.Printf("Server starting on port %s", cfg.Server.Port)
	log.Fatal(r.Run(":" + cfg.Server.Port))
}

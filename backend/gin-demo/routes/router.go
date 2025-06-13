package routes

import (
	"github.com/gin-gonic/gin"
	apiv1 "github.com/oroiteS/Campus_second-hand_trading_platform/internal/api"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/middleware"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/repository"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/service"
)

// SetupRouter 配置路由
func SetupRouter() *gin.Engine {
	r := gin.Default()

	// 依赖注入
	userRepo := repository.NewUserRepository()
	authService := service.NewAuthService(userRepo)
	authHandler := apiv1.NewAuthHandler(authService)

	// API V1 路由组
	v1 := r.Group("/api")
	{
		authRoutes := v1.Group("/auth")
		{
			authRoutes.POST("/register", authHandler.Register)
			authRoutes.POST("/login", authHandler.Login)
		}

		// 受保护的路由组示例
		protectedRoutes := v1.Group("/protected")
		protectedRoutes.Use(middleware.AuthMiddleware()) // 应用JWT认证中间件
		{
			protectedRoutes.GET("/data", authHandler.ProtectedEndpoint)
		}
	}

	return r
}

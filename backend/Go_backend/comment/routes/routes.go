package routes

import (
	"github.com/gin-gonic/gin"

	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/comment/handlers"
)

// SetupRoutes 设置路由
func SetupRoutes(r *gin.Engine, commentHandler *handlers.CommentHandler) {
	api := r.Group("/api-8091")
	{
		// 评论相关路由
		comments := api.Group("/comments")
		{
			comments.POST("", commentHandler.CreateComment)               // 创建评论
			comments.GET("", commentHandler.GetComments)                  // 获取评论列表
			comments.GET("/:message_id", commentHandler.GetCommentDetail) // 获取评论详情
			comments.DELETE("/:message_id", commentHandler.DeleteComment) // 删除评论
		}
	}
}

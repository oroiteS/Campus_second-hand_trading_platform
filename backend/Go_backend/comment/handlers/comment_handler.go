package handlers

import (
	"github.com/gin-gonic/gin"
	"net/http"

	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/comment/models"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/comment/services"
)

type CommentHandler struct {
	commentService *services.CommentService
}

// NewCommentHandler 创建评论处理器
func NewCommentHandler(commentService *services.CommentService) *CommentHandler {
	return &CommentHandler{
		commentService: commentService,
	}
}

// CreateComment 创建评论
// @Summary 创建评论
// @Description 用户对商品进行评论或回复
// @Tags 评论管理
// @Accept json
// @Produce json
// @Param comment body models.CreateCommentRequest true "评论信息"
// @Success 200 {object} models.CommentResponse
// @Failure 400 {object} map[string]interface{}
// @Failure 500 {object} map[string]interface{}
// @Router /comments [post]
func (h *CommentHandler) CreateComment(c *gin.Context) {
	var req models.CreateCommentRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error":   "参数错误",
			"message": err.Error(),
		})
		return
	}

	response, err := h.commentService.CreateComment(c.Request.Context(), &req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error":   "创建评论失败",
			"message": err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message": "评论创建成功",
		"data":    response,
	})
}

// GetComments 获取商品评论列表
// @Summary 获取商品评论列表
// @Description 根据商品ID获取评论列表，支持分页
// @Tags 评论管理
// @Accept json
// @Produce json
// @Param commodity_id query string true "商品ID"
// @Param page query int false "页码" default(1)
// @Param page_size query int false "每页数量" default(10)
// @Param sort_by query string false "排序字段" default(created_at)
// @Param order query string false "排序方式" default(desc)
// @Success 200 {object} map[string]interface{}
// @Failure 400 {object} map[string]interface{}
// @Failure 500 {object} map[string]interface{}
// @Router /comments [get]
func (h *CommentHandler) GetComments(c *gin.Context) {
	var req models.GetCommentsRequest
	if err := c.ShouldBindQuery(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error":   "参数错误",
			"message": err.Error(),
		})
		return
	}

	// 设置默认值
	if req.Page <= 0 {
		req.Page = 1
	}
	if req.PageSize <= 0 {
		req.PageSize = 10
	}
	if req.SortBy == "" {
		req.SortBy = "created_at"
	}
	if req.Order == "" {
		req.Order = "desc"
	}

	comments, total, err := h.commentService.GetCommentsByCommodity(c.Request.Context(), &req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error":   "获取评论列表失败",
			"message": err.Error(),
		})
		return
	}

	// 计算分页信息
	totalPages := (total + int64(req.PageSize) - 1) / int64(req.PageSize)

	c.JSON(http.StatusOK, gin.H{
		"message": "获取评论列表成功",
		"data": gin.H{
			"comments":    comments,
			"total":       total,
			"page":        req.Page,
			"page_size":   req.PageSize,
			"total_pages": totalPages,
			"has_next":    req.Page < int(totalPages),
			"has_prev":    req.Page > 1,
		},
	})
}

// DeleteComment 删除评论
// @Summary 删除评论
// @Description 用户删除自己的评论
// @Tags 评论管理
// @Accept json
// @Produce json
// @Param message_id path string true "消息ID"
// @Param user_id query string true "用户ID"
// @Success 200 {object} map[string]interface{}
// @Failure 400 {object} map[string]interface{}
// @Failure 500 {object} map[string]interface{}
// @Router /comments/{message_id} [delete]
func (h *CommentHandler) DeleteComment(c *gin.Context) {
	messageID := c.Param("message_id")
	userID := c.Query("user_id")

	if messageID == "" || userID == "" {
		c.JSON(http.StatusBadRequest, gin.H{
			"error":   "参数错误",
			"message": "消息ID和用户ID不能为空",
		})
		return
	}

	err := h.commentService.DeleteComment(c.Request.Context(), messageID, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error":   "删除评论失败",
			"message": err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message": "评论删除成功",
	})
}

// GetCommentDetail 获取评论详情
// @Summary 获取评论详情
// @Description 根据消息ID获取评论详情
// @Tags 评论管理
// @Accept json
// @Produce json
// @Param message_id path string true "消息ID"
// @Success 200 {object} models.CommentResponse
// @Failure 400 {object} map[string]interface{}
// @Failure 500 {object} map[string]interface{}
// @Router /comments/{message_id} [get]
func (h *CommentHandler) GetCommentDetail(c *gin.Context) {
	messageID := c.Param("message_id")
	if messageID == "" {
		c.JSON(http.StatusBadRequest, gin.H{
			"error":   "参数错误",
			"message": "消息ID不能为空",
		})
		return
	}

	comment, err := h.commentService.GetCommentByMessageID(c.Request.Context(), messageID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error":   "获取评论详情失败",
			"message": err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message": "获取评论详情成功",
		"data":    comment,
	})
}

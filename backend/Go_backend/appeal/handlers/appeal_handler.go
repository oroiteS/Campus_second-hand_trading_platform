package handlers

import (
	"github.com/gin-gonic/gin"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/models"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/services"
	"net/http"
)

type AppealHandler struct {
	appealService *services.AppealService
}

func NewAppealHandler(appealService *services.AppealService) *AppealHandler {
	return &AppealHandler{appealService: appealService}
}

// GetAppealStatusByOrderId 通过订单ID查看申诉状态
func (h *AppealHandler) GetAppealStatusByOrderId(c *gin.Context) {
	orderId := c.Param("orderId")
	if orderId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "订单ID不能为空"})
		return
	}

	status, err := h.appealService.GetAppealStatusByOrderId(orderId)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "查询申诉状态失败"})
		return
	}

	response := models.AppealStatusResponse{Status: status}
	c.JSON(http.StatusOK, response)
}

// BatchGetAppealStatusByOrderIds 批量查询申诉状态
func (h *AppealHandler) BatchGetAppealStatusByOrderIds(c *gin.Context) {
	var req models.BatchGetAppealStatusRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "请求参数错误: " + err.Error()})
		return
	}

	if len(req.OrderIds) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "订单ID列表不能为空"})
		return
	}

	statusMap, err := h.appealService.BatchGetAppealStatusByOrderIds(req.OrderIds)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "查询申诉状态失败"})
		return
	}

	c.JSON(http.StatusOK, statusMap)
}

// CreateAppeal 发起申诉请求
func (h *AppealHandler) CreateAppeal(c *gin.Context) {
	var req models.CreateAppealRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "请求参数错误: " + err.Error()})
		return
	}

	appeal, err := h.appealService.CreateAppeal(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "创建申诉失败"})
		return
	}

	c.JSON(http.StatusCreated, appeal)
}

// CancelAppeal 取消申诉
func (h *AppealHandler) CancelAppeal(c *gin.Context) {
	argumentId := c.Param("argumentId")
	if argumentId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "申诉ID不能为空"})
		return
	}

	// 从请求中获取用户ID（实际项目中应该从JWT token或session中获取）
	userId := c.GetHeader("User-ID") // 这里假设用户ID通过header传递
	if userId == "" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "用户未认证"})
		return
	}

	err := h.appealService.CancelAppeal(argumentId, userId)
	if err != nil {
		if err.Error() == "record not found" {
			c.JSON(http.StatusNotFound, gin.H{"error": "申诉记录不存在或无权限操作"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "取消申诉失败"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "申诉已取消"})
}

// AdminUpdateAppealStatus 管理员更新申诉状态
func (h *AppealHandler) AdminUpdateAppealStatus(c *gin.Context) {
	argumentId := c.Param("argumentId")
	if argumentId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "申诉ID不能为空"})
		return
	}

	var req models.AdminUpdateAppealRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "请求参数错误: " + err.Error()})
		return
	}

	// 验证状态值是否有效
	if req.Status != "finish" && req.Status != "refuse" && req.Status != "process" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "无效的状态值，只能是finish、refuse或process"})
		return
	}

	appeal, err := h.appealService.AdminUpdateAppealStatus(argumentId, req)
	if err != nil {
		if err.Error() == "record not found" {
			c.JSON(http.StatusNotFound, gin.H{"error": "申诉记录不存在"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "更新申诉状态失败"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message": "申诉状态更新成功",
		"appeal":  appeal,
	})
}

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
// @Summary 通过订单ID查看申诉状态
// @Description 根据订单ID查询该订单的申诉状态
// @Tags 申诉管理
// @Accept json
// @Produce json
// @Param orderId path string true "订单ID"
// @Success 200 {object} models.AppealStatusResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /appeals/order/{orderId} [get]
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
// @Summary 批量查询申诉状态
// @Description 根据订单ID列表批量查询申诉状态
// @Tags 申诉管理
// @Accept json
// @Produce json
// @Param request body models.BatchGetAppealStatusRequest true "批量查询请求"
// @Success 200 {object} map[string]*string
// @Failure 400 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /appeals/batch-status [post]
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
// @Summary 发起申诉请求
// @Description 用户发起一个新的申诉请求
// @Tags 申诉管理
// @Accept json
// @Produce json
// @Param request body models.CreateAppealRequest true "创建申诉请求"
// @Success 201 {object} models.Appeal
// @Failure 400 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /appeals [post]
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
// @Summary 取消申诉
// @Description 用户取消自己发起的申诉
// @Tags 申诉管理
// @Accept json
// @Produce json
// @Param argumentId path string true "申诉ID"
// @Param User-ID header string true "用户ID"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /appeals/{argumentId}/cancel [put]
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
// @Summary 管理员更新申诉状态
// @Description 管理员更新申诉的处理状态
// @Tags 申诉管理
// @Accept json
// @Produce json
// @Param argumentId path string true "申诉ID"
// @Param request body models.AdminUpdateAppealRequest true "更新申诉状态请求"
// @Success 200 {object} models.AdminUpdateResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /appeals/{argumentId}/admin-update [put]
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

// GetAllAppeals 获取所有申诉记录
// @Summary 获取所有申诉记录
// @Description 获取数据库中所有申诉记录
// @Tags 申诉管理
// @Accept json
// @Produce json
// @Success 200 {object} models.AppealListResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /appeals/all [get]
func (h *AppealHandler) GetAllAppeals(c *gin.Context) {
	appeals, err := h.appealService.GetAllAppeals()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "查询申诉记录失败"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"appeals": appeals,
		"count":   len(appeals),
	})
}

// BatchGetAppeals 批量查询申诉记录
// @Summary 批量查询申诉记录
// @Description 根据订单ID列表批量查询申诉记录详情
// @Tags 申诉管理
// @Accept json
// @Produce json
// @Param request body models.BatchGetAppealStatusRequest true "批量查询请求"
// @Success 200 {object} models.AppealListResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /appeals/batch-appeals [post]
func (h *AppealHandler) BatchGetAppeals(c *gin.Context) {
	var req models.BatchGetAppealStatusRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "请求参数错误: " + err.Error()})
		return
	}

	appeals, err := h.appealService.BatchGetAppeals(req.OrderIds)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "查询申诉记录失败"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"appeals": appeals,
		"count":   len(appeals),
	})
}

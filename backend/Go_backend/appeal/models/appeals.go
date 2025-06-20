package models

import (
	"time"
)

// Appeal 申诉模型
type Appeal struct {
	ArgumentId string    `gorm:"column:Argument_Id;type:char(36);primaryKey" json:"argumentId"`
	Argue1Id   string    `gorm:"column:Argue1_id;type:char(9);not null" json:"argue1Id"`
	Argue2Id   *string   `gorm:"column:Argue2_id;type:char(9)" json:"argue2Id"`
	OrderId    *string   `gorm:"column:order_id;type:char(36)" json:"orderId"`
	Reason     string    `gorm:"column:Reason;type:text;not null" json:"reason"`
	CreatedAt  time.Time `gorm:"column:created_at;autoCreateTime" json:"createdAt"`
	RootId     *string   `gorm:"column:Root_id;type:char(9)" json:"rootId"`
	Status     string    `gorm:"column:status;type:enum('finish','refuse','process');default:process" json:"status"`
}

// CreateAppealRequest 创建申诉请求
type CreateAppealRequest struct {
	Argue1Id string  `json:"argue1Id" binding:"required"`
	Argue2Id *string `json:"argue2Id"`
	OrderId  *string `json:"orderId"`
	Reason   string  `json:"reason" binding:"required"`
	Status   string  `json:"status"`
}

// AppealStatusResponse 申诉状态响应
type AppealStatusResponse struct {
	Status *string `json:"status"`
}

// AdminUpdateAppealRequest 管理员更新申诉请求
type AdminUpdateAppealRequest struct {
	Status string `json:"status" binding:"required"`
	RootId string `json:"rootId" binding:"required"`
}

// BatchGetAppealStatusRequest 批量获取申诉状态请求
type BatchGetAppealStatusRequest struct {
	OrderIds []string `json:"orderIds" binding:"required"`
}

// AppealListResponse 申诉列表响应
type AppealListResponse struct {
	Appeals []Appeal `json:"appeals"`
	Count   int      `json:"count"`
}

// AdminUpdateResponse 管理员更新响应
type AdminUpdateResponse struct {
	Message string `json:"message"`
	Appeal  Appeal `json:"appeal"`
}

// ErrorResponse 错误响应
type ErrorResponse struct {
	Error string `json:"error"`
}

// SuccessResponse 成功响应
type SuccessResponse struct {
	Message string `json:"message"`
}

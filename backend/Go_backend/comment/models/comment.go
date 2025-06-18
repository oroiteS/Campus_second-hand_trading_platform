package models

import (
	"github.com/google/uuid"
	"gorm.io/gorm"
	"time"
)

// Comment 评论模型
type Comment struct {
	ID               uint      `json:"id" gorm:"primaryKey;autoIncrement;comment:评论ID，主键"`
	MessageID        string    `json:"message_id" gorm:"type:char(36);not null;uniqueIndex;comment:消息ID（UUID）"`
	CommodityID      string    `json:"commodity_id" gorm:"type:char(36);not null;index:idx_commodity_id;comment:商品ID，外键指向commodities表"`
	UserID           string    `json:"user_id" gorm:"type:char(9);not null;index:idx_user_id;comment:用户ID，外键指向users表"`
	Message          string    `json:"message" gorm:"type:varchar(2000);not null;comment:评论内容"`
	ReplyToMessageID *string   `json:"reply_to_message_id" gorm:"type:char(36);index:idx_reply_to_message_id;comment:回复的消息ID（用于回复功能）"`
	MessageType      string    `json:"message_type" gorm:"type:enum('comment','reply');not null;index:idx_message_type;comment:消息类型：评论或回复"`
	CreatedAt        time.Time `json:"created_at" gorm:"autoCreateTime;index:idx_created_at;comment:创建时间"`
	IsDeleted        bool      `json:"is_deleted" gorm:"default:0;index:idx_is_deleted;comment:是否删除，0为未删除，1为已删除"`

	// 关联字段
	Replies []Comment `json:"replies,omitempty" gorm:"foreignKey:ReplyToMessageID;references:MessageID"`
}

// TableName 指定表名
func (Comment) TableName() string {
	return "comments"
}

// BeforeCreate 创建前钩子，生成UUID
func (c *Comment) BeforeCreate(tx *gorm.DB) error {
	if c.MessageID == "" {
		c.MessageID = uuid.New().String()
	}
	return nil
}

// CreateCommentRequest 创建评论请求
type CreateCommentRequest struct {
	CommodityID      string `json:"commodity_id" binding:"required" validate:"uuid4"`
	UserID           string `json:"user_id" binding:"required"`
	Message          string `json:"message" binding:"required,max=2000"`
	ReplyToMessageID string `json:"reply_to_message_id,omitempty" validate:"omitempty,uuid4"`
}

// CommentResponse 评论响应
type CommentResponse struct {
	ID               uint              `json:"id"`
	MessageID        string            `json:"message_id"`
	CommodityID      string            `json:"commodity_id"`
	UserID           string            `json:"user_id"`
	Message          string            `json:"message"`
	ReplyToMessageID *string           `json:"reply_to_message_id"`
	MessageType      string            `json:"message_type"`
	CreatedAt        time.Time         `json:"created_at"`
	Replies          []CommentResponse `json:"replies,omitempty"`
}

// GetCommentsRequest 获取评论请求
type GetCommentsRequest struct {
	CommodityID string `form:"commodity_id" binding:"required"`
	Page        int    `form:"page,default=1" binding:"min=1"`
	PageSize    int    `form:"page_size,default=10" binding:"min=1,max=100"`
	SortBy      string `form:"sort_by,default=created_at" binding:"oneof=created_at id"`
	Order       string `form:"order,default=desc" binding:"oneof=asc desc"`
}

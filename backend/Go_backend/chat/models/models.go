package models

import (
	"time"
)

// User 用户模型
type User struct {
	UserID       string    `gorm:"column:User_ID;primaryKey;size:9" json:"user_id"`
	UserName     string    `gorm:"column:User_name;size:20;not null" json:"user_name"`
	Password     string    `gorm:"column:password;size:64;not null" json:"-"`
	Telephone    string    `gorm:"column:telephone;size:11;not null" json:"telephone"`
	RealName     string    `gorm:"column:real_name;size:50;not null" json:"real_name"`
	AvatarURL    *string   `gorm:"column:avatar_url;size:255" json:"avatar_url"`
	Longitude    float64   `gorm:"column:User_Loc_longitude;type:decimal(9,6);not null" json:"longitude"`
	Latitude     float64   `gorm:"column:User_Loc_latitude;type:decimal(9,6);not null" json:"latitude"`
	UserStatus   bool      `gorm:"column:User_sta;default:false" json:"user_status"`
	CreateAt     time.Time `gorm:"column:Create_at;default:CURRENT_TIMESTAMP" json:"create_at"`
	IDCard       string    `gorm:"column:ID;size:18;not null" json:"-"`
}

// TableName 指定表名
func (User) TableName() string {
	return "users"
}

// ChatSession 聊天会话模型
type ChatSession struct {
	SessionID     string    `gorm:"column:session_id;primaryKey;size:36" json:"session_id"`
	BuyerID       string    `gorm:"column:buyer_id;size:9;not null" json:"buyer_id"`
	SellerID      string    `gorm:"column:seller_id;size:9;not null" json:"seller_id"`
	SessionStatus string    `gorm:"column:session_status;type:enum('active','closed');default:'active'" json:"session_status"`
	CreatedAt     time.Time `gorm:"column:created_at;default:CURRENT_TIMESTAMP" json:"created_at"`
	UpdatedAt     time.Time `gorm:"column:updated_at;default:CURRENT_TIMESTAMP" json:"updated_at"`

	// 关联字段
	Buyer    User `gorm:"foreignKey:BuyerID;references:UserID" json:"buyer,omitempty"`
	Seller   User `gorm:"foreignKey:SellerID;references:UserID" json:"seller,omitempty"`
	Messages []ChatMessage `gorm:"foreignKey:SessionID;references:SessionID" json:"messages,omitempty"`
}

// TableName 指定表名
func (ChatSession) TableName() string {
	return "chat_sessions"
}

// ChatMessage 聊天消息模型
type ChatMessage struct {
	MessageID   string     `gorm:"column:message_id;primaryKey;size:36" json:"message_id"`
	SessionID   string     `gorm:"column:session_id;size:36;not null" json:"session_id"`
	SenderID    string     `gorm:"column:sender_id;size:9;not null" json:"sender_id"`
	ReceiverID  string     `gorm:"column:receiver_id;size:9;not null" json:"receiver_id"`
	MessageType string     `gorm:"column:message_type;type:enum('text','image','file');default:'text'" json:"message_type"`
	Content     string     `gorm:"column:content;type:text;not null" json:"content"`
	FileURL     *string    `gorm:"column:file_url;size:500" json:"file_url,omitempty"`
	ReadStatus  bool       `gorm:"column:read_status;default:false" json:"read_status"`
	SentAt      time.Time  `gorm:"column:sent_at;default:CURRENT_TIMESTAMP" json:"sent_at"`
	ReadAt      *time.Time `gorm:"column:read_at" json:"read_at,omitempty"`

	// 关联字段
	Sender   User        `gorm:"foreignKey:SenderID;references:UserID" json:"sender,omitempty"`
	Receiver User        `gorm:"foreignKey:ReceiverID;references:UserID" json:"receiver,omitempty"`
	Session  ChatSession `gorm:"foreignKey:SessionID;references:SessionID" json:"session,omitempty"`
}

// TableName 指定表名
func (ChatMessage) TableName() string {
	return "chat_messages"
}

// CreateSessionRequest 创建会话请求
type CreateSessionRequest struct {
	BuyerID  string `json:"buyer_id" binding:"required"`
	SellerID string `json:"seller_id" binding:"required"`
}

// SendMessageRequest 发送消息请求
type SendMessageRequest struct {
	SessionID   string `json:"session_id" binding:"required"`
	SenderID    string `json:"sender_id" binding:"required"`
	ReceiverID  string `json:"receiver_id" binding:"required"`
	MessageType string `json:"message_type" binding:"required,oneof=text image file"`
	Content     string `json:"content" binding:"required"`
	FileURL     string `json:"file_url,omitempty"`
}

// WebSocketMessage WebSocket消息结构
type WebSocketMessage struct {
	Type    string      `json:"type"` // message, notification, error
	Data    interface{} `json:"data"`
	UserID  string      `json:"user_id,omitempty"`
	Time    time.Time   `json:"time"`
}

// UnreadCountResponse 未读消息数量响应
type UnreadCountResponse struct {
	UserID      string `json:"user_id"`
	UnreadCount int64  `json:"unread_count"`
}
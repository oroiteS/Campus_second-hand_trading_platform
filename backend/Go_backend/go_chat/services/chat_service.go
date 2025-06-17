package services

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/go-redis/redis/v8"
	"github.com/google/uuid"
	"gorm.io/gorm"

	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/go_chat/models"
)

// ChatService 聊天服务
type ChatService struct {
	db  *gorm.DB
	rdb *redis.Client
}

// NewChatService 创建聊天服务实例
func NewChatService(db *gorm.DB, rdb *redis.Client) *ChatService {
	return &ChatService{
		db:  db,
		rdb: rdb,
	}
}

// CreateSession 创建聊天会话
func (s *ChatService) CreateSession(buyerID, sellerID string) (*models.ChatSession, error) {
	// 检查是否已存在会话
	var existingSession models.ChatSession
	err := s.db.Where("buyer_id = ? AND seller_id = ?", buyerID, sellerID).First(&existingSession).Error
	if err == nil {
		// 会话已存在，返回现有会话
		return &existingSession, nil
	}

	// 创建新会话
	session := &models.ChatSession{
		SessionID:     uuid.New().String(),
		BuyerID:       buyerID,
		SellerID:      sellerID,
		SessionStatus: "active",
		CreatedAt:     time.Now(),
		UpdatedAt:     time.Now(),
	}

	err = s.db.Create(session).Error
	if err != nil {
		return nil, fmt.Errorf("failed to create session: %w", err)
	}

	return session, nil
}

// GetUserSessions 获取用户的所有会话
func (s *ChatService) GetUserSessions(userID string) ([]models.ChatSession, error) {
	var sessions []models.ChatSession
	err := s.db.Preload("Buyer").Preload("Seller").
		Where("buyer_id = ? OR seller_id = ?", userID, userID).
		Order("updated_at DESC").
		Find(&sessions).Error

	if err != nil {
		return nil, fmt.Errorf("failed to get user sessions: %w", err)
	}

	return sessions, nil
}

// GetSessionMessages 获取会话消息
func (s *ChatService) GetSessionMessages(sessionID string, limit, offset int) ([]models.ChatMessage, error) {
	var messages []models.ChatMessage
	query := s.db.Preload("Sender").Preload("Receiver").
		Where("session_id = ?", sessionID).
		Order("sent_at DESC")

	if limit > 0 {
		query = query.Limit(limit)
	}
	if offset > 0 {
		query = query.Offset(offset)
	}

	err := query.Find(&messages).Error
	if err != nil {
		return nil, fmt.Errorf("failed to get session messages: %w", err)
	}

	// 反转消息顺序，使最新的消息在最后
	for i, j := 0, len(messages)-1; i < j; i, j = i+1, j-1 {
		messages[i], messages[j] = messages[j], messages[i]
	}

	return messages, nil
}

// SendMessage 发送消息
func (s *ChatService) SendMessage(req *models.SendMessageRequest) (*models.ChatMessage, error) {
	// 验证会话是否存在
	var session models.ChatSession
	err := s.db.Where("session_id = ?", req.SessionID).First(&session).Error
	if err != nil {
		return nil, fmt.Errorf("session not found: %w", err)
	}

	// 创建消息
	message := &models.ChatMessage{
		MessageID:   uuid.New().String(),
		SessionID:   req.SessionID,
		SenderID:    req.SenderID,
		ReceiverID:  req.ReceiverID,
		MessageType: req.MessageType,
		Content:     req.Content,
		ReadStatus:  false,
		SentAt:      time.Now(),
	}

	if req.FileURL != "" {
		message.FileURL = &req.FileURL
	}

	// 保存到数据库
	err = s.db.Create(message).Error
	if err != nil {
		return nil, fmt.Errorf("failed to save message: %w", err)
	}

	// 更新会话的最后更新时间
	s.db.Model(&session).Update("updated_at", time.Now())

	// 预加载发送者和接收者信息
	s.db.Preload("Sender").Preload("Receiver").First(message, "message_id = ?", message.MessageID)

	// 缓存到Redis（可选，用于快速查询最近消息）
	s.cacheRecentMessage(message)

	// 增加未读消息计数
	s.incrementUnreadCount(req.ReceiverID)

	return message, nil
}

// MarkAsRead 标记消息为已读
func (s *ChatService) MarkAsRead(messageID, userID string) error {
	now := time.Now()
	err := s.db.Model(&models.ChatMessage{}).
		Where("message_id = ? AND receiver_id = ?", messageID, userID).
		Updates(map[string]interface{}{
			"read_status": true,
			"read_at":     &now,
		}).Error

	if err != nil {
		return fmt.Errorf("failed to mark message as read: %w", err)
	}

	// 减少未读消息计数
	s.decrementUnreadCount(userID)

	return nil
}

// GetUnreadCount 获取用户未读消息数量
func (s *ChatService) GetUnreadCount(userID string) (int64, error) {
	// 先尝试从Redis获取
	ctx := context.Background()
	key := fmt.Sprintf("unread_count:%s", userID)
	count, err := s.rdb.Get(ctx, key).Int64()
	if err == nil {
		return count, nil
	}

	// Redis中没有，从数据库查询
	var dbCount int64
	err = s.db.Model(&models.ChatMessage{}).
		Where("receiver_id = ? AND read_status = false", userID).
		Count(&dbCount).Error

	if err != nil {
		return 0, fmt.Errorf("failed to get unread count: %w", err)
	}

	// 缓存到Redis
	s.rdb.Set(ctx, key, dbCount, time.Hour*24)

	return dbCount, nil
}

// cacheRecentMessage 缓存最近的消息到Redis
func (s *ChatService) cacheRecentMessage(message *models.ChatMessage) {
	ctx := context.Background()
	key := fmt.Sprintf("recent_messages:%s", message.SessionID)

	msgData, err := json.Marshal(message)
	if err != nil {
		return
	}

	// 添加到列表头部
	s.rdb.LPush(ctx, key, msgData)
	// 保持列表长度不超过100条
	s.rdb.LTrim(ctx, key, 0, 99)
	// 设置过期时间
	s.rdb.Expire(ctx, key, time.Hour*24)
}

// incrementUnreadCount 增加未读消息计数
func (s *ChatService) incrementUnreadCount(userID string) {
	ctx := context.Background()
	key := fmt.Sprintf("unread_count:%s", userID)
	s.rdb.Incr(ctx, key)
	s.rdb.Expire(ctx, key, time.Hour*24)
}

// decrementUnreadCount 减少未读消息计数
func (s *ChatService) decrementUnreadCount(userID string) {
	ctx := context.Background()
	key := fmt.Sprintf("unread_count:%s", userID)
	count, err := s.rdb.Get(ctx, key).Int64()
	if err != nil || count <= 0 {
		s.rdb.Set(ctx, key, 0, time.Hour*24)
		return
	}
	s.rdb.Decr(ctx, key)
}

// GetUserInfo 获取用户信息
func (s *ChatService) GetUserInfo(userID string) (*models.User, error) {
	var user models.User
	err := s.db.Where("user_id = ?", userID).First(&user).Error
	if err != nil {
		return nil, fmt.Errorf("user not found: %w", err)
	}
	return &user, nil
}

package services

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/go-redis/redis/v8"
	"gorm.io/gorm"

	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/comment/models"
)

type CommentService struct {
	db  *gorm.DB
	rdb *redis.Client
}

// NewCommentService 创建评论服务
func NewCommentService(db *gorm.DB, rdb *redis.Client) *CommentService {
	return &CommentService{db: db, rdb: rdb}
}

// CreateComment 创建评论
func (s *CommentService) CreateComment(ctx context.Context, req *models.CreateCommentRequest) (*models.CommentResponse, error) {
	// 验证商品是否存在
	var commodityExists bool
	err := s.db.Raw("SELECT EXISTS(SELECT 1 FROM commodities WHERE commodity_id = ?)", req.CommodityID).Scan(&commodityExists).Error
	if err != nil {
		return nil, fmt.Errorf("检查商品存在性失败: %w", err)
	}
	if !commodityExists {
		return nil, fmt.Errorf("商品不存在")
	}

	// 验证用户是否存在
	var userExists bool
	err = s.db.Raw("SELECT EXISTS(SELECT 1 FROM users WHERE User_ID = ?)", req.UserID).Scan(&userExists).Error
	if err != nil {
		return nil, fmt.Errorf("检查用户存在性失败: %w", err)
	}
	if !userExists {
		return nil, fmt.Errorf("用户不存在")
	}

	// 确定消息类型
	messageType := "comment"
	if req.ReplyToMessageID != "" {
		messageType = "reply"
		// 验证被回复的评论是否存在
		var replyExists bool
		err = s.db.Model(&models.Comment{}).Where("message_id = ? AND is_deleted = 0", req.ReplyToMessageID).Select("count(*) > 0").Find(&replyExists).Error
		if err != nil {
			return nil, fmt.Errorf("检查被回复评论存在性失败: %w", err)
		}
		if !replyExists {
			return nil, fmt.Errorf("被回复的评论不存在")
		}
	}

	// 创建评论
	comment := &models.Comment{
		CommodityID: req.CommodityID,
		UserID:      req.UserID,
		Message:     req.Message,
		MessageType: messageType,
	}

	if req.ReplyToMessageID != "" {
		comment.ReplyToMessageID = &req.ReplyToMessageID
	}

	err = s.db.Create(comment).Error
	if err != nil {
		return nil, fmt.Errorf("创建评论失败: %w", err)
	}

	// 清除相关缓存
	s.clearCommentCache(ctx, req.CommodityID)

	return s.convertToResponse(comment), nil
}

// GetCommentsByCommodity 根据商品ID获取评论列表
func (s *CommentService) GetCommentsByCommodity(ctx context.Context, req *models.GetCommentsRequest) ([]models.CommentResponse, int64, error) {
	// 尝试从缓存获取
	cacheKey := fmt.Sprintf("comments:%s:page:%d:size:%d:sort:%s:%s", req.CommodityID, req.Page, req.PageSize, req.SortBy, req.Order)
	cachedData, err := s.rdb.Get(ctx, cacheKey).Result()
	if err == nil {
		var result struct {
			Comments []models.CommentResponse `json:"comments"`
			Total    int64                    `json:"total"`
		}
		if json.Unmarshal([]byte(cachedData), &result) == nil {
			return result.Comments, result.Total, nil
		}
	}

	// 从数据库查询
	var comments []models.Comment
	var total int64

	// 计算偏移量
	offset := (req.Page - 1) * req.PageSize

	// 构建查询
	query := s.db.Model(&models.Comment{}).Where("commodity_id = ? AND is_deleted = 0", req.CommodityID)

	// 获取总数
	err = query.Count(&total).Error
	if err != nil {
		return nil, 0, fmt.Errorf("获取评论总数失败: %w", err)
	}

	// 获取评论列表（只获取顶级评论）
	err = query.Where("message_type = ?", "comment").
		Order(fmt.Sprintf("%s %s", req.SortBy, req.Order)).
		Offset(offset).
		Limit(req.PageSize).
		Find(&comments).Error
	if err != nil {
		return nil, 0, fmt.Errorf("获取评论列表失败: %w", err)
	}

	// 获取每个评论的回复
	var responses []models.CommentResponse
	for _, comment := range comments {
		response := s.convertToResponse(&comment)

		// 获取回复
		var replies []models.Comment
		err = s.db.Where("reply_to_message_id = ? AND is_deleted = 0", comment.MessageID).
			Order("created_at ASC").
			Find(&replies).Error
		if err == nil {
			for _, reply := range replies {
				response.Replies = append(response.Replies, *s.convertToResponse(&reply))
			}
		}

		responses = append(responses, *response)
	}

	// 缓存结果
	cacheData := struct {
		Comments []models.CommentResponse `json:"comments"`
		Total    int64                    `json:"total"`
	}{
		Comments: responses,
		Total:    total,
	}
	if data, err := json.Marshal(cacheData); err == nil {
		s.rdb.Set(ctx, cacheKey, data, 5*time.Minute)
	}

	return responses, total, nil
}

// DeleteComment 删除评论（软删除）
func (s *CommentService) DeleteComment(ctx context.Context, messageID, userID string) error {
	// 验证评论是否存在且属于该用户
	var comment models.Comment
	err := s.db.Where("message_id = ? AND user_id = ? AND is_deleted = 0", messageID, userID).First(&comment).Error
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return fmt.Errorf("评论不存在或无权限删除")
		}
		return fmt.Errorf("查询评论失败: %w", err)
	}

	// 软删除评论
	err = s.db.Model(&comment).Update("is_deleted", 1).Error
	if err != nil {
		return fmt.Errorf("删除评论失败: %w", err)
	}

	// 清除缓存
	s.clearCommentCache(ctx, comment.CommodityID)

	return nil
}

// GetCommentByMessageID 根据消息ID获取评论详情
func (s *CommentService) GetCommentByMessageID(ctx context.Context, messageID string) (*models.CommentResponse, error) {
	var comment models.Comment
	err := s.db.Where("message_id = ? AND is_deleted = 0", messageID).First(&comment).Error
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("评论不存在")
		}
		return nil, fmt.Errorf("查询评论失败: %w", err)
	}

	return s.convertToResponse(&comment), nil
}

// convertToResponse 转换为响应格式
func (s *CommentService) convertToResponse(comment *models.Comment) *models.CommentResponse {
	return &models.CommentResponse{
		ID:               comment.ID,
		MessageID:        comment.MessageID,
		CommodityID:      comment.CommodityID,
		UserID:           comment.UserID,
		Message:          comment.Message,
		ReplyToMessageID: comment.ReplyToMessageID,
		MessageType:      comment.MessageType,
		CreatedAt:        comment.CreatedAt,
		Replies:          []models.CommentResponse{},
	}
}

// clearCommentCache 清除评论相关缓存
func (s *CommentService) clearCommentCache(ctx context.Context, commodityID string) {
	pattern := fmt.Sprintf("comments:%s:*", commodityID)
	keys, err := s.rdb.Keys(ctx, pattern).Result()
	if err == nil && len(keys) > 0 {
		s.rdb.Del(ctx, keys...)
	}
}

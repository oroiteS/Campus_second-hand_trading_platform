package services

import (
	"errors"
	"github.com/google/uuid"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/appeal/models"
	"gorm.io/gorm"
)

type AppealService struct {
	db *gorm.DB
}

func NewAppealService(db *gorm.DB) *AppealService {
	return &AppealService{db: db}
}

// GetAppealStatusByOrderId 通过订单ID查看申诉状态
func (s *AppealService) GetAppealStatusByOrderId(orderId string) (*string, error) {
	var appeal models.Appeal
	err := s.db.Where("order_id = ?", orderId).First(&appeal).Error
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil // 没有找到申诉记录，返回null
		}
		return nil, err
	}
	return &appeal.Status, nil
}

// CreateAppeal 发起申诉请求
func (s *AppealService) CreateAppeal(req models.CreateAppealRequest) (*models.Appeal, error) {
	// 生成UUID v7作为申诉ID
	argumentId := uuid.New().String()

	// 创建申诉记录，status默认为process
	appeal := models.Appeal{
		ArgumentId: argumentId,
		Argue1Id:   req.Argue1Id,
		Argue2Id:   req.Argue2Id,
		OrderId:    req.OrderId,
		Reason:     req.Reason,
		Status:     "process", // 强制设置为process，忽略前端传入的status
	}

	err := s.db.Create(&appeal).Error
	if err != nil {
		return nil, err
	}

	return &appeal, nil
}

// CancelAppeal 取消申诉（将状态改为finish）
func (s *AppealService) CancelAppeal(argumentId string, userId string) error {
	// 只允许申诉发起者取消申诉
	result := s.db.Model(&models.Appeal{}).Where("argument_id = ? AND argue1_id = ?", argumentId, userId).Update("status", "finish")
	if result.Error != nil {
		return result.Error
	}
	if result.RowsAffected == 0 {
		return gorm.ErrRecordNotFound
	}
	return nil
}

// AdminUpdateAppealStatus 管理员更新申诉状态
func (s *AppealService) AdminUpdateAppealStatus(argumentId string, req models.AdminUpdateAppealRequest) (*models.Appeal, error) {
	// 更新申诉状态和管理员ID
	updateData := map[string]interface{}{
		"status":  req.Status,
		"root_id": req.RootId,
	}

	result := s.db.Model(&models.Appeal{}).Where("argument_id = ?", argumentId).Updates(updateData)
	if result.Error != nil {
		return nil, result.Error
	}
	if result.RowsAffected == 0 {
		return nil, gorm.ErrRecordNotFound
	}

	// 返回更新后的申诉记录
	var appeal models.Appeal
	err := s.db.Where("argument_id = ?", argumentId).First(&appeal).Error
	if err != nil {
		return nil, err
	}

	return &appeal, nil
}

// BatchGetAppealStatusByOrderIds 批量查询申诉状态
func (s *AppealService) BatchGetAppealStatusByOrderIds(orderIds []string) (map[string]*string, error) {
	var appeals []models.Appeal
	err := s.db.Where("order_id IN ?", orderIds).Find(&appeals).Error
	if err != nil {
		return nil, err
	}

	statusMap := make(map[string]*string)
	// 初始化所有请求的orderId，默认状态为null
	for _, orderId := range orderIds {
		statusMap[orderId] = nil
	}

	// 填充找到的申诉状态
	for _, appeal := range appeals {
		if appeal.OrderId != nil {
			status := appeal.Status
			statusMap[*appeal.OrderId] = &status
		}
	}

	return statusMap, nil
}

// BatchGetAppeals BatchGetAppeals批量查询申诉
func (s *AppealService) BatchGetAppeals(orderIds []string) ([]models.Appeal, error) {
	var appeals []models.Appeal
	if len(orderIds) == 0 {
		// 如果没有提供订单ID，返回空列表
		return appeals, nil
	}

	err := s.db.Where("order_id IN ?", orderIds).Find(&appeals).Error
	if err != nil {
		return nil, err
	}

	return appeals, nil
}

// GetAllAppeals 获取所有申诉记录
func (s *AppealService) GetAllAppeals() ([]models.Appeal, error) {
	var appeals []models.Appeal
	err := s.db.Find(&appeals).Error
	if err != nil {
		return nil, err
	}

	return appeals, nil
}

// GetAllAppealsWithPagination 分页获取所有申诉记录
func (s *AppealService) GetAllAppealsWithPagination(page, pageSize int) ([]models.Appeal, int64, error) {
	var appeals []models.Appeal
	var total int64

	// 计算总数
	err := s.db.Model(&models.Appeal{}).Count(&total).Error
	if err != nil {
		return nil, 0, err
	}

	// 分页查询
	offset := (page - 1) * pageSize
	err = s.db.Offset(offset).Limit(pageSize).Find(&appeals).Error
	if err != nil {
		return nil, 0, err
	}

	return appeals, total, nil
}

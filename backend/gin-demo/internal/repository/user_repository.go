package repository

import (
	"errors"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/models"
	"github.com/oroiteS/Campus_second-hand_trading_platform/pkg/database"
	"gorm.io/gorm"
)

// UserRepository 定义用户仓库接口
type UserRepository interface {
	CreateUser(user *models.User) error
	GetUserByUserID(userID string) (*models.User, error)
	GetUserByUsername(username string) (*models.User, error)
}

// userRepository 结构体实现了 UserRepository 接口
type userRepository struct {
	db *gorm.DB
}

// NewUserRepository 创建一个新的 userRepository 实例
func NewUserRepository() UserRepository {
	return &userRepository{db: database.DB}
}

// CreateUser 创建用户
func (r *userRepository) CreateUser(user *models.User) error {
	return r.db.Create(user).Error
}

// GetUserByUserID 根据 UserID 查询用户
func (r *userRepository) GetUserByUserID(userID string) (*models.User, error) {
	var user models.User
	if err := r.db.Where("userid = ?", userID).First(&user).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil // 用户不存在
		}
		return nil, err // 其他数据库错误
	}
	return &user, nil
}

// GetUserByUsername 根据 Username 查询用户
func (r *userRepository) GetUserByUsername(username string) (*models.User, error) {
	var user models.User
	if err := r.db.Where("username = ?", username).First(&user).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil // 用户不存在
		}
		return nil, err // 其他数据库错误
	}
	return &user, nil
}

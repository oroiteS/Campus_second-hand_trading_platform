package service

import (
	"errors"
	"fmt"

	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/api/request"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/api/response"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/models"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/repository"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/utils"
)

// AuthService 定义认证服务接口
type AuthService interface {
	Register(req request.RegisterRequest) (*response.UserResponse, error)
	Login(req request.LoginRequest) (*response.LoginResponse, error)
}

// authService 结构体实现了 AuthService 接口
type authService struct {
	userRepo repository.UserRepository
}

// NewAuthService 创建一个新的 authService 实例
func NewAuthService(userRepo repository.UserRepository) AuthService {
	return &authService{userRepo: userRepo}
}

// Register 用户注册逻辑
func (s *authService) Register(req request.RegisterRequest) (*response.UserResponse, error) {
	// 检查 UserID 是否已存在
	existingUser, _ := s.userRepo.GetUserByUserID(req.UserID)
	if existingUser != nil {
		return nil, errors.New("userid already exists")
	}

	// 检查 Username 是否已存在
	existingUser, _ = s.userRepo.GetUserByUsername(req.Username)
	if existingUser != nil {
		return nil, errors.New("username already exists")
	}

	hashedPassword, err := utils.HashPassword(req.Password)
	if err != nil {
		return nil, fmt.Errorf("failed to hash password: %w", err)
	}

	userRole := "user"
	if req.Role != "" {
		userRole = req.Role
	}

	newUser := &models.User{
		UserID:   req.UserID,
		Username: req.Username,
		Password: hashedPassword,
		Role:     userRole,
	}

	if err := s.userRepo.CreateUser(newUser); err != nil {
		return nil, fmt.Errorf("failed to create user: %w", err)
	}

	return &response.UserResponse{
		UserID:   newUser.UserID,
		Username: newUser.Username,
		Role:     newUser.Role,
	}, nil
}

// Login 用户登录逻辑
func (s *authService) Login(req request.LoginRequest) (*response.LoginResponse, error) {
	var user *models.User
	var err error

	// 尝试通过 UserID 登录
	user, _ = s.userRepo.GetUserByUserID(req.Identifier)
	if user == nil {
		// 如果 UserID 未找到，尝试通过 Username 登录
		user, err = s.userRepo.GetUserByUsername(req.Identifier)
		if err != nil {
			return nil, fmt.Errorf("error fetching user by username: %w", err)
		}
		if user == nil {
			return nil, errors.New("invalid userid/username or password")
		}
	}

	if !utils.CheckPasswordHash(req.Password, user.Password) {
		return nil, errors.New("invalid userid/username or password")
	}

	token, err := utils.GenerateToken(user.UserID, user.Role)
	if err != nil {
		return nil, fmt.Errorf("failed to generate token: %w", err)
	}

	return &response.LoginResponse{
		Token: token,
		User: response.UserResponse{
			UserID:   user.UserID,
			Username: user.Username,
			Role:     user.Role,
		},
	}, nil
}

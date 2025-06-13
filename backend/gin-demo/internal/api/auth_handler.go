package v1

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/api/request"
	"github.com/oroiteS/Campus_second-hand_trading_platform/internal/service"
)

// AuthHandler 结构体包含认证相关的处理器方法
type AuthHandler struct {
	authService service.AuthService
}

// NewAuthHandler 创建一个新的 AuthHandler 实例
func NewAuthHandler(authService service.AuthService) *AuthHandler {
	return &AuthHandler{authService: authService}
}

// Register godoc
// @Summary User registration
// @Description Register a new user
// @Tags auth
// @Accept  json
// @Produce  json
// @Param   user body request.RegisterRequest true "User registration details"
// @Success 201 {object} response.UserResponse "Successfully registered"
// @Failure 400 {object} map[string]string "Invalid input"
// @Failure 500 {object} map[string]string "Internal server error"
// @Router /auth/register [post]
func (h *AuthHandler) Register(c *gin.Context) {
	var req request.RegisterRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid input: " + err.Error()})
		return
	}

	userResp, err := h.authService.Register(req)
	if err != nil {
		// 根据错误类型返回不同的状态码，例如用户已存在等
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, userResp)
}

// Login godoc
// @Summary User login
// @Description Log in an existing user
// @Tags auth
// @Accept  json
// @Produce  json
// @Param   credentials body request.LoginRequest true "User login credentials"
// @Success 200 {object} response.LoginResponse "Successfully logged in"
// @Failure 400 {object} map[string]string "Invalid input"
// @Failure 401 {object} map[string]string "Unauthorized"
// @Failure 500 {object} map[string]string "Internal server error"
// @Router /auth/login [post]
func (h *AuthHandler) Login(c *gin.Context) {
	var req request.LoginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid input: " + err.Error()})
		return
	}

	loginResp, err := h.authService.Login(req)
	if err != nil {
		if err.Error() == "invalid userid/username or password" {
			c.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, loginResp)
}

// ProtectedEndpoint godoc
// @Summary Access a protected endpoint
// @Description This endpoint requires JWT authentication.
// @Tags protected
// @Security ApiKeyAuth
// @Produce  json
// @Success 200 {object} map[string]string "Successfully accessed"
// @Failure 401 {object} map[string]string "Unauthorized"
// @Router /protected/data [get]
func (h *AuthHandler) ProtectedEndpoint(c *gin.Context) {
	userID, _ := c.Get("userID")
	userRole, _ := c.Get("userRole")

	c.JSON(http.StatusOK, gin.H{
		"message": "Welcome to the protected area!",
		"userID":  userID,
		"role":    userRole,
	})
}

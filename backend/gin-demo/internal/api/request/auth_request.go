package request

// RegisterRequest 注册请求结构体
type RegisterRequest struct {
	UserID   string `json:"userid" binding:"required,min=3,max=15"`
	Username string `json:"username" binding:"required,min=3,max=20"`
	Password string `json:"password" binding:"required,min=6,max=30"`
	Role     string `json:"role" binding:"omitempty,oneof=user admin"` // omitempty 表示可选，默认为 user
}

// LoginRequest 登录请求结构体
type LoginRequest struct {
	Identifier string `json:"identifier" binding:"required"` // 可以是 UserID 或 Username
	Password   string `json:"password" binding:"required"`
}

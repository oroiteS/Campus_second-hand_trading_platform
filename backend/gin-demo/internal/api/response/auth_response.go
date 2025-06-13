package response

// UserResponse 用户信息响应 (不包含密码)
type UserResponse struct {
	UserID   string `json:"userid"`
	Username string `json:"username"`
	Role     string `json:"role"`
}

// LoginResponse 登录成功响应
type LoginResponse struct {
	Token string       `json:"token"`
	User  UserResponse `json:"user"`
}

package models

import (
	"time"
)

// User 定义了用户模型
type User struct {
	ID        uint      `gorm:"primaryKey;autoIncrement" json:"-"`
	UserID    string    `gorm:"column:userid;type:varchar(15);uniqueIndex;not null" json:"userid"`
	Username  string    `gorm:"type:varchar(20);not null" json:"username"`
	Password  string    `gorm:"type:varchar(255);not null" json:"-"`
	Role      string    `gorm:"type:enum('user','admin');default:'user';not null" json:"role"`
	CreatedAt time.Time `gorm:"autoCreateTime" json:"created_at"`
	UpdatedAt time.Time `gorm:"autoUpdateTime" json:"updated_at"`
}

// TableName 指定 User 模型对应的数据库表名
func (User) TableName() string {
	return "users"
}

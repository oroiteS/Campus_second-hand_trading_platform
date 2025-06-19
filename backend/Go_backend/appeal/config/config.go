package config

import (
	"github.com/joho/godotenv"
	"log"
	"os"
)

// Config 应用配置结构
type Config struct {
	Server ServerConfig
	MySQL  MySQLConfig
}

// ServerConfig 服务器配置
type ServerConfig struct {
	Port string
}

// MySQLConfig MySQL数据库配置
type MySQLConfig struct {
	Host     string
	Port     string
	User     string
	Password string
	Database string
}

// Load 加载配置
func Load() *Config {
	// 加载.env文件
	if err := godotenv.Load(); err != nil {
		log.Printf("警告: 无法加载.env文件: %v", err)
	}

	return &Config{
		Server: ServerConfig{
			Port: getEnv("SERVER_PORT", "8088"),
		},
		MySQL: MySQLConfig{
			Host:     getEnv("MYSQL_HOST", "localhost"),
			Port:     getEnv("MYSQL_PORT", "3306"),
			User:     getEnv("MYSQL_USER", "root"),
			Password: getEnv("MYSQL_PASSWORD", ""),
			Database: getEnv("MYSQL_DATABASE", "campus"),
		},
	}
}

// getEnv 获取环境变量，如果不存在则返回默认值
func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}

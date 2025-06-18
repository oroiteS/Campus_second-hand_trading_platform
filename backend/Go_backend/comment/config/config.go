package config

import (
	"os"
)

// Config 应用配置结构
type Config struct {
	Server ServerConfig
	MySQL  MySQLConfig
	Redis  RedisConfig
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

// RedisConfig Redis配置
type RedisConfig struct {
	Host     string
	Port     string
	Username string
	Password string
	DB       int
}

// Load 加载配置
func Load() *Config {
	return &Config{
		Server: ServerConfig{
			Port: getEnv("SERVER_PORT", "8091"),
		},
		MySQL: MySQLConfig{
			Host:     getEnv("MYSQL_HOST", "rm-cn-smw4b7vtl0001ho.rwlb.rds.aliyuncs.com"),
			Port:     getEnv("MYSQL_PORT", "3306"),
			User:     getEnv("MYSQL_USER", "campus_test"),
			Password: getEnv("MYSQL_PASSWORD", "Campus_suep2022"),
			Database: getEnv("MYSQL_DATABASE", "campus"),
		},
		Redis: RedisConfig{
			Host:     getEnv("REDIS_HOST", "r-uf683p0x96aj2ht6whpd.redis.rds.aliyuncs.com"),
			Port:     getEnv("REDIS_PORT", "6379"),
			Username: getEnv("REDIS_USERNAME", "campus_test"),
			Password: getEnv("REDIS_PASSWORD", "Campus_suep2022"),
			DB:       1,
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

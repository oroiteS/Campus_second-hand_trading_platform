package config

import "os"

// Config 应用配置结构
type Config struct {
	Server  ServerConfig
	MySQL   MySQLConfig
	Redis   RedisConfig
	MongoDB MongoDBConfig
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
	Password string
	DB       int
}

// MongoDBConfig MongoDB配置
type MongoDBConfig struct {
}

// Load 加载配置
func Load() *Config {
	return &Config{
		Server: ServerConfig{
			Port: getEnv("SERVER_PORT", "8091"),
		},
		MySQL: MySQLConfig{
			Host:     getEnv("MYSQL_HOST", "localhost"),
			Port:     getEnv("MYSQL_PORT", "3306"),
			User:     getEnv("MYSQL_USER", "campus_test"),
			Password: getEnv("MYSQL_PASSWORD", "campus_suep"),
			Database: getEnv("MYSQL_DATABASE", "campus"),
		},
		Redis: RedisConfig{
			Host:     getEnv("REDIS_HOST", "localhost"),
			Port:     getEnv("REDIS_PORT", "6379"),
			Password: getEnv("REDIS_PASSWORD", "123456"),
			DB:       0,
		},
		MongoDB: MongoDBConfig{},
	}
}

// getEnv 获取环境变量，如果不存在则返回默认值
func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}

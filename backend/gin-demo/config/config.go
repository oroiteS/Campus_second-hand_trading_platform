package config

import (
	"log"
	"os"

	"github.com/joho/godotenv"
)

// Config 结构体用于存储所有配置项
type Config struct {
	DBHost     string
	DBPort     string
	DBUser     string
	DBPassword string
	DBName     string
	JWTSecret  string
}

var AppConfig *Config

// LoadConfig 加载配置
func LoadConfig() {
	// 实际项目中，可以考虑从 .env 文件、环境变量或配置文件服务加载
	err := godotenv.Load() // 尝试加载 .env 文件，如果不存在也不会报错
	if err != nil {
		log.Println("No .env file found, using default or environment variables")
	}

	AppConfig = &Config{
		DBHost:     getEnv("DB_HOST", "127.0.0.1"),
		DBPort:     getEnv("DB_PORT", "3306"),
		DBUser:     getEnv("DB_USER", "root"),
		DBPassword: getEnv("DB_PASSWORD", "your_password"),  // 请替换为您的数据库密码
		DBName:     getEnv("DB_NAME", "campus_trade"),       // 请替换为您的数据库名
		JWTSecret:  getEnv("JWT_SECRET", "your-secret-key"), // 请替换为您的JWT密钥
	}
	log.Println("Configuration loaded successfully")
}

func getEnv(key, fallback string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return fallback
}

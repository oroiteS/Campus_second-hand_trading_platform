# 校园二手交易平台

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Web-green.svg)](#)
[![Backend](https://img.shields.io/badge/backend-Go%20%7C%20Java%20%7C%20Python-orange.svg)](#)
[![Frontend](https://img.shields.io/badge/frontend-Vue.js-brightgreen.svg)](#)

> 这是一个功能丰富的校园二手交易平台，旨在为学生提供一个方便、安全的在线市场，用于买卖二手商品。

## 📋 目录

- [项目结构](#项目结构)
- [后端服务](#backend---后端服务)
  - [Go 微服务](#go_backend)
  - [Java 核心服务](#java_backend)
  - [Python 服务](#python)
- [前端应用](#frontend---前端应用)
- [多智能体协作平台](#mcp---多智能体协作平台)
- [项目文档](#docs---项目文档)
- [快速开始](#快速开始)
- [详细运行指南](#详细运行指南)
- [贡献指南](#贡献指南)

## 🏗️ 项目结构

项目采用前后端分离的微服务架构，主要分为以下几个部分：

```
Campus_second-hand_trading_platform/
├── backend/           # 后端服务
│   ├── Go_backend/    # Go 微服务
│   ├── Java_backend/  # Java 核心服务
│   ├── python/        # Python 服务
│   └── sql/           # 数据库脚本
├── frontend/          # 前端应用
│   ├── Administrator/ # 管理员端
│   └── Users/         # 用户端
├── mcp/              # 多智能体协作平台
├── docs/             # 项目文档
└── README.md         # 项目说明
```

### 📁 主要目录说明

| 目录 | 描述 | 技术栈 |
|------|------|--------|
| **`backend/`** | 后端服务，包含多语言微服务实现 | Go, Java, Python |
| **`frontend/`** | 前端应用，包含用户端和管理员端 | Vue.js, JavaScript |
| **`mcp/`** | 多智能体协作平台，实现智能化功能 | Python, AI/ML |
| **`docs/`** | 项目文档，包含需求分析和系统设计 | Markdown, 图表 |

---

## 🔧 backend - 后端服务

后端采用微服务架构，整合了多种语言和框架，以满足不同业务场景的需求。

### Go_backend

使用 Go 语言实现的高性能微服务，主要负责：

- **`appeal/`** - 申诉处理服务
  - 处理用户申诉请求
  - 申诉状态管理
  - 申诉结果通知

- **`chat/`** - 实时聊天服务
  - WebSocket 实时通信
  - 消息存储与检索
  - 在线状态管理

- **`comment/`** - 商品评论服务
  - 评论发布与管理
  - 评论审核机制
  - 评分统计功能

### Java_backend

使用 Java 和 Spring Boot 实现的核心业务服务，包括：

#### 🔐 认证与授权
- **`Admin_login/`** - 管理员登录服务
- **`Login/`** - 用户登录与注册服务

#### 📢 内容管理
- **`Announcement/`** - 公告管理服务
- **`Ban/`** - 用户封禁管理服务

#### 🛒 交易功能
- **`Cart/`** - 购物车功能服务
- **`order-management/`** - 订单管理服务
- **`wallet/`** - 用户钱包功能服务

#### 📦 商品管理
- **`Product_management_seller/`** - 卖家商品管理服务
- **`View_latest/`** - 最新商品查看服务
- **`View_product_information/`** - 商品详细信息服务
- **`product-query/`** - 商品查询服务

#### 🌐 系统服务
- **`gateway/`** - API 网关，统一处理请求路由
- **`nearBy/`** - 附近商品/用户功能服务
- **`profile/`** - 用户个人资料管理服务

### python

使用 Python、FastAPI 和 SQLAlchemy 实现的服务，专注于：

- **`app/`** - 商品的 CRUD (创建、读取、更新、删除) 操作
  - RESTful API 接口
  - 数据验证与序列化
  - 异步处理支持

- **`embedding_recommendation/`** - 基于嵌入向量的商品推荐系统
  - 机器学习推荐算法
  - 用户行为分析
  - 个性化推荐引擎

### sql

包含数据库初始化脚本和相关数据：

```sql
-- 主要脚本文件
init_database.sql      # 数据库初始化脚本
commodities_ini.sql    # 商品数据初始化
ini.txt               # 配置说明文件
```

---

## 🎨 frontend - 前端应用

前端包含两个独立的 Vue.js 应用，分别服务于普通用户和管理员。

### Administrator - 管理员端

提供对整个平台的管理功能：

- **功能特性**
  - 👥 用户管理 - 用户信息查看、编辑、封禁
  - 📦 商品审核 - 商品上架审核、违规处理
  - 📢 公告发布 - 平台公告编辑与发布
  - 📊 数据统计 - 平台运营数据分析

- **技术架构**
  - **`src/router/`** - 管理员端路由配置
  - **`src/views/`** - 管理页面视图组件
  - **`src/components/`** - 可复用组件库

### Users - 用户端

提供给学生用户的完整交易体验：

- **功能特性**
  - 🔍 商品浏览 - 分类浏览、搜索筛选
  - 🛒 购买流程 - 加入购物车、下单支付
  - 📝 商品发布 - 二手商品上架销售
  - 💬 交流互动 - 实时聊天、评论互动

- **技术架构**
  - **`src/components/`** - 可重用的 UI 组件
  - **`src/views/`** - 用户界面主要视图
  - **`src/api/`** - 后端服务交互模块

---

## 🤖 mcp - 多智能体协作平台

这是一个基于 Python 的高级功能模块，旨在通过 AI 技术提升用户体验。

### 核心组件

- **`qwen3_mcp_apply.py`** - 大型语言模型应用入口
  - 集成 Qwen 模型
  - 智能对话处理
  - 自然语言理解

- **`server/sql-mcp-server/`** - SQL 多智能体任务服务器
  - 数据库查询优化
  - 智能 SQL 生成
  - 查询性能分析

- **`webUI.py`** - Gradio Web 交互界面
  - 可视化调试工具
  - 模型测试平台
  - 参数调优界面

- **`workspace/tools/`** - 智能体工具集
  - 代码解释器
  - 安全执行环境
  - 工具链管理

---

## 📚 docs - 项目文档

该目录存放了项目开发过程中的所有相关文档。

### 📊 系统设计图

- **`img/`** 目录包含：
  - **`用例图.png`** - 展示不同角色用户与系统的交互关系
  - **`系统结构图.png`** - 描绘整个系统的技术架构
  - **`类图.png`** - 定义系统中主要实体及其关系
  - **`顶层图.png`** - 系统整体架构概览

### 📋 需求文档

- **`需求分析.md`** - 详细描述项目的功能和非功能性需求

---

## 🚀 快速开始

### 环境要求

- **Node.js** >= 16.0.0
- **Java** >= 11
- **Go** >= 1.19
- **Python** >= 3.9
- **MySQL** >= 8.0
- **Redis** >= 6.0

### 一键启动（推荐）

```bash
# 克隆项目
git clone https://github.com/your-username/Campus_second-hand_trading_platform.git
cd Campus_second-hand_trading_platform

# 初始化数据库
mysql -u root -p < backend/sql/init_database.sql

# 启动所有服务（需要 Docker）
docker-compose up -d
```

---

## 📖 详细运行指南

由于项目包含多个独立的服务和应用，请按照以下步骤逐步启动：

### 1. 📊 数据库初始化

```sql
-- 连接 MySQL 数据库
mysql -u root -p

-- 执行初始化脚本
source backend/sql/init_database.sql;
source backend/sql/commodities_ini.sql;
```

### 2. 🔧 后端服务启动

#### Go 服务

```bash
# 申诉服务
cd backend/Go_backend/appeal
go mod tidy
go run main.go

# 聊天服务
cd ../chat
go mod tidy
go run main.go

# 评论服务
cd ../comment
go mod tidy
go run main.go
```

#### Java 服务

```bash
# API 网关（必须首先启动）
cd backend/Java_backend/gateway
mvn clean install
mvn spring-boot:run

# 其他服务（可并行启动）
cd ../Login
mvn spring-boot:run

# 重复以上步骤启动其他 Java 服务
```

#### Python 服务

```bash
cd backend/python

# 使用 uv 安装依赖（推荐）
uv sync

# 启动 FastAPI 服务
uv run uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### 3. 🎨 前端应用启动

#### 用户端

```bash
cd frontend/Users
npm install
npm run serve
```

#### 管理员端

```bash
cd frontend/Administrator
npm install
npm run serve
```

### 4. 🤖 MCP 平台启动

```bash
cd mcp

# 使用 uv 安装依赖
uv sync

# 启动 Web UI
uv run python webUI.py

# 启动 MCP 服务器
cd server/sql-mcp-server
uv run python main.py
```

### 5. 🌐 访问地址

启动完成后，可通过以下地址访问：

| 服务 | 地址 | 说明 |
|------|------|------|
| 用户端 | http://localhost:8080 | 学生用户界面 |
| 管理员端 | http://localhost:8081 | 管理员界面 |
| API 网关 | http://localhost:9999 | 后端 API 入口 |
| Python API | http://localhost:8000 | FastAPI 文档 |
| MCP Web UI | http://localhost:7860 | AI 平台界面 |

---

## 🤝 贡献指南

我们欢迎所有形式的贡献！请阅读以下指南：

### 开发流程

1. **Fork** 本仓库
2. **创建** 特性分支 (`git checkout -b feature/AmazingFeature`)
3. **提交** 更改 (`git commit -m 'Add some AmazingFeature'`)
4. **推送** 到分支 (`git push origin feature/AmazingFeature`)
5. **创建** Pull Request

### 代码规范

- **Go**: 遵循 `gofmt` 和 `golint` 规范
- **Java**: 遵循 Google Java Style Guide
- **Python**: 遵循 PEP 8 规范
- **JavaScript**: 遵循 ESLint 配置

### 提交信息规范

```
type(scope): description

[optional body]

[optional footer]
```

**类型说明：**
- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

---

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

---

## 📞 联系我们

如果您有任何问题或建议，请通过以下方式联系我们：

- 📧 邮箱：[your-email@example.com](mailto:your-email@example.com)
- 🐛 问题反馈：[GitHub Issues](https://github.com/your-username/Campus_second-hand_trading_platform/issues)
- 💬 讨论：[GitHub Discussions](https://github.com/your-username/Campus_second-hand_trading_platform/discussions)

---

<div align="center">

**⭐ 如果这个项目对您有帮助，请给我们一个 Star！⭐**

*希望这份文档能帮助您更好地理解和使用这个项目！*

</div>


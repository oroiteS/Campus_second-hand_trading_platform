# 校园二手交易平台

这是一个功能丰富的校园二手交易平台，旨在为学生提供一个方便、安全的在线市场，用于买卖二手商品。

## 项目结构

项目采用前后端分离的架构，主要分为以下几个部分：

- **`backend/`**: 后端服务，包含 Go、Java 和 Python 实现的微服务。
- **`frontend/`**: 前端应用，包含面向普通用户和管理员的两个独立 Vue.js 项目。
- **`mcp/`**: 多智能体协作平台（Multi-Agent Collaboration Platform），用于实现智能化的推荐、搜索等功能。
- **`docs/`**: 项目文档，包含需求分析、系统设计图等。

---

### `backend` - 后端服务

后端采用微服务架构，整合了多种语言和框架，以满足不同业务场景的需求。

- **`Go_backend/`**: 使用 Go 语言实现的微服务，主要负责：
  - **`appeal/`**: 申诉处理。
  - **`chat/`**: 实时聊天。
  - **`comment/`**: 商品评论。

- **`Java_backend/`**: 使用 Java 和 Spring Boot 实现的核心业务服务，包括：
  - **`Admin_login/`**: 管理员登录。
  - **`Announcement/`**: 公告管理。
  - **`Ban/`**: 用户封禁管理。
  - **`Cart/`**: 购物车功能。
  - **`Login/`**: 用户登录与注册。
  - **`Product_management_seller/`**: 卖家商品管理。
  - **`View_latest/`**: 查看最新商品。
  - **`View_product_information/`**: 查看商品详细信息。
  - **`gateway/`**: API 网关，统一处理请求路由。
  - **`nearBy/`**: 附近商品/用户功能。
  - **`order-management/`**: 订单管理。
  - **`product-query/`**: 商品查询。
  - **`profile/`**: 用户个人资料管理。
  - **`wallet/`**: 用户钱包功能。

- **`python/`**: 使用 Python、FastAPI 和 SQLAlchemy 实现的服务，专注于：
  - **`app/`**: 商品的 CRUD (创建、读取、更新、删除) 操作。
  - **`embedding_recommendation/`**: 基于嵌入向量的商品推荐系统。

- **`sql/`**: 包含数据库初始化脚本和相关数据。

---

### `frontend` - 前端应用

前端包含两个独立的 Vue.js 应用，分别服务于普通用户和管理员。

- **`Administrator/`**: 管理员端，提供对整个平台的管理功能，例如用户管理、商品审核、公告发布等。
  - **`src/router/`**: 定义了管理员端的路由。
  - **`src/views/`**: 包含了各个管理页面的视图组件。

- **`Users/`**: 用户端，提供给学生用户进行商品浏览、搜索、购买、发布和交流。
  - **`src/components/`**: 可重用的 UI 组件。
  - **`src/views/`**: 用户界面的主要视图。
  - **`src/api/`**: 用于与后端服务进行交互的 API 请求模块。

---

### `mcp` - 多智能体协作平台

这是一个基于 Python 的高级功能模块，旨在通过 AI 技术提升用户体验。

- **`qwen3_mcp_apply.py`**: 该文件应用了某个大型语言模型（如 Qwen）的示例或入口，包含了与模型交互的代码。
- **`server/sql-mcp-server/`**: 一个独立的服务器，用于处理与 SQL 相关的多智能体任务。
- **`webUI.py`**: 提供了一个 Gradio Web 界面，方便与 MCP 进行交互和调试。
- **`workspace/tools/code_interpreter/`**: 可能包含一个代码解释器工具，供智能体在安全环境中执行代码。

---

### `docs` - 项目文档

该目录存放了项目开发过程中的所有相关文档。

- **`img/`**: 包含了系统设计图，如：
  - **`用例图.png`**: 展示了不同角色的用户与系统的交互。
  - **`系统结构图.png`**: 描绘了整个系统的架构。
  - **`类图.png`**: 定义了系统中的主要实体及其关系。
- **`需求分析.md`**: 详细描述了项目的功能和非功能性需求。

---

## 如何运行

由于项目包含多个独立的服务和应用，请分别参考每个子目录下的 `README.md` 文件以获取详细的运行指南。

通常的步骤如下：

1.  **数据库**: 运行 `backend/sql/init_database.sql` 来初始化数据库。
2.  **后端**: 分别进入 `Go_backend`、`Java_backend` 和 `python` 的各个子目录，按照其说明文档启动服务。
3.  **前端**: 分别进入 `frontend/Administrator` 和 `frontend/Users` 目录，执行 `npm install` 和 `npm run serve` 来启动前端应用。
4.  **MCP**: 根据 `mcp/README.md` 的指引启动相关服务。

希望这份文档能帮助您更好地理解和使用这个项目！


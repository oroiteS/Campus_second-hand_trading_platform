package handlers

import (
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"

	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/go_chat/models"
	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/go_chat/services"
)

// ChatHandler 聊天处理器
type ChatHandler struct {
	chatService   *services.ChatService
	webSocketHub  *services.WebSocketHub
}

// NewChatHandler 创建聊天处理器
func NewChatHandler(chatService *services.ChatService, webSocketHub *services.WebSocketHub) *ChatHandler {
	return &ChatHandler{
		chatService:  chatService,
		webSocketHub: webSocketHub,
	}
}

// HandleWebSocket 处理WebSocket连接
// @Summary WebSocket连接
// @Description 建立WebSocket连接用于实时聊天
// @Tags WebSocket
// @Param user_id path string true "用户ID"
// @Success 101 {string} string "Switching Protocols"
// @Router /ws/{user_id} [get]
func (h *ChatHandler) HandleWebSocket(c *gin.Context) {
	userID := c.Param("user_id")
	if userID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "User ID is required"})
		return
	}

	h.webSocketHub.HandleWebSocket(c.Writer, c.Request, userID)
}

// CreateSession 创建聊天会话
// @Summary 创建聊天会话
// @Description 在买家和卖家之间创建聊天会话
// @Tags Chat
// @Accept json
// @Produce json
// @Param request body models.CreateSessionRequest true "创建会话请求"
// @Success 200 {object} models.ChatSession
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /chat/sessions [post]
func (h *ChatHandler) CreateSession(c *gin.Context) {
	var req models.CreateSessionRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	session, err := h.chatService.CreateSession(req.BuyerID, req.SellerID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, session)
}

// GetUserSessions 获取用户的所有会话
// @Summary 获取用户会话列表
// @Description 获取指定用户的所有聊天会话
// @Tags Chat
// @Produce json
// @Param user_id path string true "用户ID"
// @Success 200 {array} models.ChatSession
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /chat/user/{user_id}/sessions [get]
func (h *ChatHandler) GetUserSessions(c *gin.Context) {
	userID := c.Param("user_id")
	if userID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "User ID is required"})
		return
	}

	sessions, err := h.chatService.GetUserSessions(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, sessions)
}

// GetSessionMessages 获取会话消息
// @Summary 获取会话消息
// @Description 获取指定会话的消息列表
// @Tags Chat
// @Produce json
// @Param session_id path string true "会话ID"
// @Param limit query int false "限制数量" default(50)
// @Param offset query int false "偏移量" default(0)
// @Success 200 {array} models.ChatMessage
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /chat/session/{session_id}/messages [get]
func (h *ChatHandler) GetSessionMessages(c *gin.Context) {
	sessionID := c.Param("session_id")
	if sessionID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Session ID is required"})
		return
	}

	// 解析查询参数
	limit, _ := strconv.Atoi(c.DefaultQuery("limit", "50"))
	offset, _ := strconv.Atoi(c.DefaultQuery("offset", "0"))

	messages, err := h.chatService.GetSessionMessages(sessionID, limit, offset)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, messages)
}

// SendMessage 发送消息
// @Summary 发送消息
// @Description 发送聊天消息
// @Tags Chat
// @Accept json
// @Produce json
// @Param request body models.SendMessageRequest true "发送消息请求"
// @Success 200 {object} models.ChatMessage
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /chat/messages [post]
func (h *ChatHandler) SendMessage(c *gin.Context) {
	var req models.SendMessageRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	message, err := h.chatService.SendMessage(&req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	// 通过WebSocket实时发送消息给接收者
	if h.webSocketHub.IsUserOnline(req.ReceiverID) {
		wsMessage := models.WebSocketMessage{
			Type:   "message",
			Data:   message,
			UserID: req.SenderID,
			Time:   time.Now(),
		}
		h.webSocketHub.SendToUser(req.ReceiverID, wsMessage)
	}

	c.JSON(http.StatusOK, message)
}

// MarkAsRead 标记消息为已读
// @Summary 标记消息为已读
// @Description 将指定消息标记为已读状态
// @Tags Chat
// @Produce json
// @Param message_id path string true "消息ID"
// @Param user_id query string true "用户ID"
// @Success 200 {object} map[string]string
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /chat/messages/{message_id}/read [put]
func (h *ChatHandler) MarkAsRead(c *gin.Context) {
	messageID := c.Param("message_id")
	userID := c.Query("user_id")

	if messageID == "" || userID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Message ID and User ID are required"})
		return
	}

	err := h.chatService.MarkAsRead(messageID, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Message marked as read"})
}

// GetUnreadCount 获取未读消息数量
// @Summary 获取未读消息数量
// @Description 获取指定用户的未读消息总数
// @Tags Chat
// @Produce json
// @Param user_id path string true "用户ID"
// @Success 200 {object} models.UnreadCountResponse
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /chat/user/{user_id}/unread [get]
func (h *ChatHandler) GetUnreadCount(c *gin.Context) {
	userID := c.Param("user_id")
	if userID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "User ID is required"})
		return
	}

	count, err := h.chatService.GetUnreadCount(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	response := models.UnreadCountResponse{
		UserID:      userID,
		UnreadCount: count,
	}

	c.JSON(http.StatusOK, response)
}
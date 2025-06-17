package services

import (
	"encoding/json"
	"log"
	"net/http"
	"sync"
	"time"

	"github.com/gorilla/websocket"

	"github.com/oroiteS/Campus_second-hand_trading_platform/tree/main/backend/Go_backend/go_chat/models"
)

// WebSocketHub WebSocket连接管理中心
type WebSocketHub struct {
	chatService *ChatService
	clients     map[string]*Client // userID -> Client
	broadcast   chan []byte
	register    chan *Client
	unregister  chan *Client
	mu          sync.RWMutex
}

// Client WebSocket客户端
type Client struct {
	hub    *WebSocketHub
	conn   *websocket.Conn
	userID string
	send   chan []byte
}

// WebSocket升级器配置
var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin: func(r *http.Request) bool {
		// 允许所有来源（生产环境中应该更严格）
		return true
	},
}

// NewWebSocketHub 创建WebSocket管理中心
func NewWebSocketHub(chatService *ChatService) *WebSocketHub {
	return &WebSocketHub{
		chatService: chatService,
		clients:     make(map[string]*Client),
		broadcast:   make(chan []byte),
		register:    make(chan *Client),
		unregister:  make(chan *Client),
	}
}

// Run 运行WebSocket管理中心
func (h *WebSocketHub) Run() {
	for {
		select {
		case client := <-h.register:
			h.mu.Lock()
			h.clients[client.userID] = client
			h.mu.Unlock()
			log.Printf("User %s connected", client.userID)

			// 发送连接成功消息
			welcomeMsg := models.WebSocketMessage{
				Type:   "notification",
				Data:   "Connected successfully",
				UserID: client.userID,
				Time:   time.Now(),
			}
			h.SendToUser(client.userID, welcomeMsg)

		case client := <-h.unregister:
			h.mu.Lock()
			if _, ok := h.clients[client.userID]; ok {
				delete(h.clients, client.userID)
				close(client.send)
				log.Printf("User %s disconnected", client.userID)
			}
			h.mu.Unlock()

		case message := <-h.broadcast:
			h.mu.RLock()
			for _, client := range h.clients {
				select {
				case client.send <- message:
				default:
					close(client.send)
					delete(h.clients, client.userID)
				}
			}
			h.mu.RUnlock()
		}
	}
}

// HandleWebSocket 处理WebSocket连接
func (h *WebSocketHub) HandleWebSocket(w http.ResponseWriter, r *http.Request, userID string) {
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Printf("WebSocket upgrade failed: %v", err)
		return
	}

	client := &Client{
		hub:    h,
		conn:   conn,
		userID: userID,
		send:   make(chan []byte, 256),
	}

	client.hub.register <- client

	// 启动读写协程
	go client.writePump()
	go client.readPump()
}

// SendToUser 发送消息给指定用户
func (h *WebSocketHub) SendToUser(userID string, message models.WebSocketMessage) {
	h.mu.RLock()
	client, exists := h.clients[userID]
	h.mu.RUnlock()

	if !exists {
		return
	}

	data, err := json.Marshal(message)
	if err != nil {
		log.Printf("Failed to marshal message: %v", err)
		return
	}

	select {
	case client.send <- data:
	default:
		close(client.send)
		h.mu.Lock()
		delete(h.clients, userID)
		h.mu.Unlock()
	}
}

// BroadcastMessage 广播消息给所有在线用户
func (h *WebSocketHub) BroadcastMessage(message models.WebSocketMessage) {
	data, err := json.Marshal(message)
	if err != nil {
		log.Printf("Failed to marshal broadcast message: %v", err)
		return
	}

	h.broadcast <- data
}

// IsUserOnline 检查用户是否在线
func (h *WebSocketHub) IsUserOnline(userID string) bool {
	h.mu.RLock()
	defer h.mu.RUnlock()
	_, exists := h.clients[userID]
	return exists
}

// GetOnlineUsers 获取所有在线用户
func (h *WebSocketHub) GetOnlineUsers() []string {
	h.mu.RLock()
	defer h.mu.RUnlock()

	users := make([]string, 0, len(h.clients))
	for userID := range h.clients {
		users = append(users, userID)
	}
	return users
}

// readPump 读取WebSocket消息
func (c *Client) readPump() {
	defer func() {
		c.hub.unregister <- c
		c.conn.Close()
	}()

	// 设置读取超时
	c.conn.SetReadDeadline(time.Now().Add(60 * time.Second))
	c.conn.SetPongHandler(func(string) error {
		c.conn.SetReadDeadline(time.Now().Add(60 * time.Second))
		return nil
	})

	for {
		_, message, err := c.conn.ReadMessage()
		if err != nil {
			if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
				log.Printf("WebSocket error: %v", err)
			}
			break
		}

		// 处理接收到的消息
		var wsMsg models.WebSocketMessage
		if err := json.Unmarshal(message, &wsMsg); err != nil {
			log.Printf("Failed to unmarshal message: %v", err)
			continue
		}

		// 处理不同类型的消息
		switch wsMsg.Type {
		case "ping":
			// 心跳响应
			pongMsg := models.WebSocketMessage{
				Type:   "pong",
				Data:   "pong",
				UserID: c.userID,
				Time:   time.Now(),
			}
			c.hub.SendToUser(c.userID, pongMsg)
		case "message":
			// 处理聊天消息（这里可以添加更多逻辑）
			log.Printf("Received message from user %s: %v", c.userID, wsMsg.Data)
		}
	}
}

// writePump 写入WebSocket消息
func (c *Client) writePump() {
	ticker := time.NewTicker(54 * time.Second)
	defer func() {
		ticker.Stop()
		c.conn.Close()
	}()

	for {
		select {
		case message, ok := <-c.send:
			c.conn.SetWriteDeadline(time.Now().Add(10 * time.Second))
			if !ok {
				c.conn.WriteMessage(websocket.CloseMessage, []byte{})
				return
			}

			w, err := c.conn.NextWriter(websocket.TextMessage)
			if err != nil {
				return
			}
			w.Write(message)

			// 批量发送队列中的其他消息
			n := len(c.send)
			for i := 0; i < n; i++ {
				w.Write([]byte{10}) // 换行符的ASCII码
				w.Write(<-c.send)
			}

			if err := w.Close(); err != nil {
				return
			}

		case <-ticker.C:
			c.conn.SetWriteDeadline(time.Now().Add(10 * time.Second))
			if err := c.conn.WriteMessage(websocket.PingMessage, nil); err != nil {
				return
			}
		}
	}
}
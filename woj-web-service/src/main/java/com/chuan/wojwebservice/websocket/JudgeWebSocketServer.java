package com.chuan.wojwebservice.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chuan_wxy
 *
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{userId}")
public class JudgeWebSocketServer {

    // 存放每个用户对应的 Session
    private static final ConcurrentHashMap<Long, Session> SESSION_MAP = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        SESSION_MAP.put(userId, session);
        log.info("WebSocket 连接成功, 用户ID: {}, 当前在线人数: {}", userId, SESSION_MAP.size());
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        SESSION_MAP.remove(userId);
        log.info("WebSocket 连接关闭, 用户ID: {}", userId);
    }

    /**
     * 主动推送消息的方法
     */
    public static void sendMessage(Long userId, String message) {
        Session session = SESSION_MAP.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("推送到用户 {} 失败", userId, e);
            }
        }
    }
}
package com.example.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private ObjectMapper mapper = new ObjectMapper();

    private final Set<WebSocketSession> sessions = new HashSet<>();

    // key-value 하나 = 채팅방 하나 / chatRoomId : {session1, session2...}
    private final Map<Long,Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println(session.getId() + " 연결됨");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("payload = " + payload);

        // message -> ChatMessageDto
        ChatMessageDto messageDto = mapper.readValue(payload, ChatMessageDto.class);
        System.out.println("chatMessageDto = " + messageDto);

        Long chatRoomId = messageDto.getChatRoomId();
        // 해당 채팅방이 현재 열려있지 않으면
        if (!chatRoomSessionMap.containsKey(chatRoomId)) {
            chatRoomSessionMap.put(chatRoomId, new HashSet<>());
        }
        Set<WebSocketSession> userSessions = chatRoomSessionMap.get(chatRoomId);

        // 세션 추가 동작
        if (messageDto.getMessageType().equals(ChatMessageDto.MessageType.ENTER)) {
            userSessions.add(session);
        }
        // 1:1 채팅만 허용
        if (userSessions.size() > 2) {
            removeClosedSession(userSessions);
        }
        // broadCast message
        sendMessageToChatRoom(messageDto, userSessions);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("세션 " + session.getId() + " 연결 끊김");
        sessions.remove(session);
    }

    private void removeClosedSession(Set<WebSocketSession> userSessions) {
        userSessions.removeIf(session -> !sessions.contains(session));
    }

    private void sendMessageToChatRoom(ChatMessageDto messageDto, Set<WebSocketSession> userSessions) {
        userSessions.parallelStream().forEach(session -> sendMessage(session, messageDto));
    }

    private <T> void sendMessage(WebSocketSession session, T message) {
        System.out.println("session.getId() = " + session.getId());
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
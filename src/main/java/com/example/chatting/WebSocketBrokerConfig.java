package com.example.chatting;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버 로직 안거치고 바로 전달할 때 mapping link
        // 컨벤션 : 1대1 전송 시 /queue, 1대다로 broadcast 시 /topic
        registry.enableSimpleBroker("/queue", "/topic");
        // 처리 필요 시, 해당 prefix를 붙이면 매핑된 handler로 들어와서 처리 후 위 링크로
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket");
//                .withSockJS();
    }
}

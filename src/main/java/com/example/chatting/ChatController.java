package com.example.chatting;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller // STOMP 사용 시 Controller 방식으로 handler 구현 가능하다.
public class ChatController {

    @MessageMapping("/hello") // @RequestMapping처럼, 해당 링크로 오는 데이터를 매핑
    @SendTo("/topic/greeting") // 처리해서, messageBroker로 전달한다.
    public String greeting(String message) {
        return "this message passed ChatController - greeting " + message;
    }
}

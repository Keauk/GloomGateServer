package com.example.gloomgate.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/send/message")
    public void sendMessage(String message) {
        // Handle the message from the client
    }

    @SendTo("/topic/messages")
    public String broadcastMessage(String message) {
        return message;
    }
}
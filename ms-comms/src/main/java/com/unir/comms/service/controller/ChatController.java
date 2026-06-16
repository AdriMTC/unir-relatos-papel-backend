package com.unir.comms.service.controller;

import com.unir.comms.service.dto.ChatMessage;
import com.unir.comms.service.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final GeminiService geminiService;

    // El front envía a /app/chat.message → responde a /topic/chat
    @MessageMapping("/chat.message")
    @SendTo("/topic/chat")
    public ChatMessage handleMessage(ChatMessage incomingMessage) {
        log.info("Mensaje WebSocket de '{}': {}", incomingMessage.getSender(),
                incomingMessage.getContent());

        String aiResponse = geminiService.getResponse(incomingMessage.getContent());

        return ChatMessage.builder()
                .type(ChatMessage.MessageType.RESPONSE)
                .sender("Asistente IA")
                .content(aiResponse)
                .build();
    }
}

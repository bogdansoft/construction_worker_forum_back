package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.chat.ChatMessage;
import com.construction_worker_forum_back.service.ChatMessageService;
import com.construction_worker_forum_back.service.ChatRoomService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage message) {
        chatRoomService
                .getChatId(message.getSenderId(), message.getRecipientId())
                .ifPresent(message::setChatId);

        chatMessageService.save(message);
    }
}

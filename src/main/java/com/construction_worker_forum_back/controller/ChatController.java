package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.chat.ChatMessage;
import com.construction_worker_forum_back.model.chat.ChatNotification;
import com.construction_worker_forum_back.service.ChatMessageService;
import com.construction_worker_forum_back.service.ChatRoomService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage message) {
        chatRoomService
                .getChatId(message.getSenderId(), message.getRecipientId(), true)
                .ifPresent(message::setChatId);

        chatMessageService.save(message);

        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),
                "/queue/messages",
                ChatNotification.of(
                        message.getId(),
                        message.getSenderId(),
                        message.getRecipientId()
                )
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(@PathVariable String senderId, @PathVariable String recipientId) {
        return ResponseEntity.ok(chatMessageService.countNewMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages(@PathVariable String senderId, @PathVariable String recipientId) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage(@PathVariable String id) {
        return ResponseEntity.ok(chatMessageService.findById(id));
    }
}

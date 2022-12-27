package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.client.NotificationClient;
import com.construction_worker_forum_back.model.Notification;
import com.construction_worker_forum_back.model.chat.ChatMessage;
import com.construction_worker_forum_back.model.chat.ChatNotification;
import com.construction_worker_forum_back.service.ChatMessageService;
import com.construction_worker_forum_back.service.ChatRoomService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@AllArgsConstructor
@CrossOrigin("https://localhost:3000")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final NotificationClient notificationClient;

    @SecurityRequirement(name = "Bearer Authentication")
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage message) {
        String senderId = message.getSenderId();
        String recipientId = message.getRecipientId();
        String senderName = message.getSenderName();

        chatRoomService
                .getChatId(
                        senderId,
                        recipientId,
                        true
                )
                .ifPresent(message::setChatId);

        chatMessageService.save(message);

        messagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/messages",
                ChatNotification.of(
                        message.getId(),
                        senderId,
                        senderName,
                        message.getContent()
                )
        );

        notificationClient.sendNotification(
                        Notification.of(
                                senderName,
                                recipientId,
                                "Sent you a message",
                                "/chat",
                                false
                        )
                )
                .doOnNext(notification -> log.info("Notification Response: {}", notification))
                .doOnError(e -> log.info("Error occurred: {}", e.getMessage()))
                .subscribe();
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(@PathVariable String senderId, @PathVariable String recipientId) {
        return ResponseEntity.ok(chatMessageService.countNewMessages(senderId, recipientId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages(@PathVariable String senderId, @PathVariable String recipientId) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage(@PathVariable String id) {
        return ResponseEntity.ok(chatMessageService.findById(id));
    }
}

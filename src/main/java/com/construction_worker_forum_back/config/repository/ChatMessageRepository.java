package com.construction_worker_forum_back.config.repository;

import com.construction_worker_forum_back.model.chat.ChatMessage;
import com.construction_worker_forum_back.model.chat.MessageStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    long countBySenderIdAndRecipientIdAndStatus(String senderId, String recipientId, MessageStatus status);

    List<ChatMessage> findByChatId(String chatId);
}

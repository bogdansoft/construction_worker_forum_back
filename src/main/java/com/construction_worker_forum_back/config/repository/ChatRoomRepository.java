package com.construction_worker_forum_back.config.repository;

import com.construction_worker_forum_back.model.chat.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);

    List<ChatRoom> findChatRoomsByChatId(String chatId);
}

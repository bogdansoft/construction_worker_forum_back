package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.chat.ChatMessage;
import com.construction_worker_forum_back.model.chat.MessageStatus;
import com.construction_worker_forum_back.repository.ChatMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;
    private final MongoOperations mongoOperations;

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        return repository.save(chatMessage);
    }
}

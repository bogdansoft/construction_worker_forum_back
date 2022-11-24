package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.exception.MessageNotFoundException;
import com.construction_worker_forum_back.model.chat.ChatMessage;
import com.construction_worker_forum_back.model.chat.MessageStatus;
import com.construction_worker_forum_back.config.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    @Autowired
    private final ChatMessageRepository repository;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private MongoOperations mongoOperations;

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        return repository.save(chatMessage);
    }

    public long countNewMessages(String senderId, String recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);

        List<ChatMessage> chatMessages = chatId.map(repository::findByChatId)
                .orElse(new ArrayList<>());

        if (!chatMessages.isEmpty()) {
            updateStatus(senderId, recipientId, MessageStatus.DELIVERED);
        }

        return chatMessages;
    }

    public ChatMessage findById(String id) {
        return repository.findById(id)
                .map(chatMessage -> {
                    chatMessage.setStatus(MessageStatus.DELIVERED);
                    return repository.save(chatMessage);
                })
                .orElseThrow(() ->
                        new MessageNotFoundException(
                                String.format("Could not find message with id: %s", id)
                        )
                );
    }

    public void updateStatus(String senderId, String recipientId, MessageStatus status) {
        Criteria criteria = Criteria
                .where("senderId").is(senderId)
                .and("recipientId").is(recipientId);
        Query query = Query.query(criteria);
        Update update = Update.update("status", status);
        mongoOperations.updateMulti(query, update, ChatMessage.class);
    }
}

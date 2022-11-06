package com.construction_worker_forum_back.integration.chat;

import com.construction_worker_forum_back.exception.MessageNotFoundException;
import com.construction_worker_forum_back.model.chat.ChatMessage;
import com.construction_worker_forum_back.repository.ChatMessageRepository;
import com.construction_worker_forum_back.service.ChatMessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChatMessageServiceIntegrationTests extends MongoTestcontainersConfig {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    private ChatMessageService chatMessageService;
    private String obiWanId;
    private String lukeId;
    private String chatId;
    private ChatMessage messageToLuke;

    @BeforeEach
    void setup() {
        chatMessageService = new ChatMessageService(chatMessageRepository);

        obiWanId = "senderId";
        lukeId = "recipientId";
        chatId = String.format("%s_%s", obiWanId, lukeId);

        messageToLuke = ChatMessage.builder()
                .chatId(chatId)
                .senderId(obiWanId)
                .recipientId(lukeId)
                .senderName("Obi Wan")
                .recipientName("Luke")
                .content("Hello There!")
                .timestamp(Date.from(Instant.now()))
                .build();
    }

    @AfterEach
    void cleanup() {
        chatMessageRepository.deleteAll();
    }

    @Test
    void givenChatMessage_whenSavedToDb_thenIdIsNotNull() {

        // given

        // when
        chatMessageService.save(messageToLuke);

        // then
        String id = messageToLuke.getId();
        assertNotNull(id);

        String actualId = chatMessageRepository
                .findById(id)
                .map(ChatMessage::getId)
                .orElseThrow(() ->
                        new MessageNotFoundException("Could not find message")
                );
        assertEquals(id, actualId);
    }
}

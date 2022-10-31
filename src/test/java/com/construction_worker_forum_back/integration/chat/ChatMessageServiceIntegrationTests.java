package com.construction_worker_forum_back.integration.chat;

import com.construction_worker_forum_back.exception.MessageNotFoundException;
import com.construction_worker_forum_back.model.chat.ChatMessage;
import com.construction_worker_forum_back.repository.ChatMessageRepository;
import com.construction_worker_forum_back.service.ChatMessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Date;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class ChatMessageServiceIntegrationTests {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

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

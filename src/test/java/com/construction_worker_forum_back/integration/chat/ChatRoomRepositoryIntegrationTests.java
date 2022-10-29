package com.construction_worker_forum_back.integration.chat;

import com.construction_worker_forum_back.model.chat.ChatRoom;
import com.construction_worker_forum_back.repository.ChatRoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class ChatRoomRepositoryIntegrationTests {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo"));

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @AfterEach
    void cleanup() {
        chatRoomRepository.deleteAll();
    }

    private final String senderId = "senderId";
    private final String recipientId = "recipientId";
    private final String chatId = String.format("%s_%s", senderId, recipientId);

    private final ChatRoom senderRecipient = ChatRoom.builder()
            .chatId(chatId)
            .senderId(senderId)
            .recipientId(recipientId)
            .build();

    private final ChatRoom recipientSender = ChatRoom.builder()
            .chatId(chatId)
            .senderId(recipientId)
            .recipientId(senderId)
            .build();

    @Test
    void givenChatRooms_whenSavedToDb_thenReturnSavedChatRooms() {

        // given

        // when
        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        // then
        Optional<ChatRoom> actualChatRoomSenderRecipient = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
        Optional<ChatRoom> actualChatRoomRecipientSender = chatRoomRepository.findBySenderIdAndRecipientId(recipientId, senderId);

        assertTrue(actualChatRoomSenderRecipient.isPresent());
        ChatRoom chatRoomSenderRecipient = actualChatRoomSenderRecipient.get();
        assertEquals(chatId, chatRoomSenderRecipient.getChatId());
        assertEquals(senderId, chatRoomSenderRecipient.getSenderId());
        assertEquals(recipientId, chatRoomSenderRecipient.getRecipientId());
        assertNotNull(chatRoomSenderRecipient.getId());

        assertTrue(actualChatRoomRecipientSender.isPresent());
        ChatRoom chatRoomRecipientSender = actualChatRoomRecipientSender.get();
        assertEquals(chatId, chatRoomRecipientSender.getChatId());
        assertEquals(recipientId, chatRoomRecipientSender.getSenderId());
        assertEquals(senderId, chatRoomRecipientSender.getRecipientId());
        assertNotNull(chatRoomRecipientSender.getId());
    }

    @Test
    void givenChatRooms_whenSavedToDb_thenReturnListOfSavedRoomsByChatId() {

        // given

        // when
        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        // then
        List<ChatRoom> chatRoomsByChatId = chatRoomRepository.findChatRoomsByChatId(chatId);

        assertThat(chatRoomsByChatId)
                .hasSize(2)
                .allMatch(chatRoom -> chatRoom.getChatId().equals(chatId));
    }
}

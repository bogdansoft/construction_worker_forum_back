package com.construction_worker_forum_back.integration.chat;

import com.construction_worker_forum_back.model.chat.ChatRoom;
import com.construction_worker_forum_back.repository.ChatRoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatRoomRepositoryIntegrationTests extends MongoDbTestContainersConfig {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @AfterEach
    void cleanUp() {
        chatRoomRepository.deleteAll();
    }

    @Test
    void givenChatRooms_whenSavedToDb_thenReturnSavedChatRooms() {

        // given
        String senderId = "senderId";
        String recipientId = "recipientId";
        String chatId = String.format("%s_%s", senderId, recipientId);

        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

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

        assertTrue(actualChatRoomRecipientSender.isPresent());
        ChatRoom chatRoomRecipientSender = actualChatRoomRecipientSender.get();
        assertEquals(chatId, chatRoomRecipientSender.getChatId());
        assertEquals(recipientId, chatRoomRecipientSender.getSenderId());
        assertEquals(senderId, chatRoomRecipientSender.getRecipientId());
    }

    @Test
    void givenChatRooms_whenSavedToDb_thenReturnListOfSavedRoomsByChatId() {

        // given
        String senderId = "senderId";
        String recipientId = "recipientId";
        String chatId = String.format("%s_%s", senderId, recipientId);

        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

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

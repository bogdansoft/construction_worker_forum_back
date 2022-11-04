package com.construction_worker_forum_back.integration.chat;

import com.construction_worker_forum_back.exception.MessageNotFoundException;
import com.construction_worker_forum_back.model.chat.ChatMessage;
import com.construction_worker_forum_back.model.chat.MessageStatus;
import com.construction_worker_forum_back.repository.ChatMessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ChatMessageRepositoryIntegrationTests extends MongoTestcontainersConfig {

    @Autowired
    ChatMessageRepository chatMessageRepository;
    private String obiWanId;
    private String lukeId;
    private String chatId;
    private ChatMessage messageToLuke;
    private ChatMessage messageToObiWan1;
    private ChatMessage messageToObiWan2;

    @BeforeEach
    void setup() {
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
                .status(MessageStatus.DELIVERED)
                .build();

        messageToObiWan1 = ChatMessage.builder()
                .chatId(chatId)
                .senderId(lukeId)
                .recipientId(obiWanId)
                .senderName("Luke")
                .recipientName("Obi Wan")
                .content("Hi!")
                .timestamp(Date.from(Instant.now().plusSeconds(1)))
                .status(MessageStatus.DELIVERED)
                .build();

        messageToObiWan2 = ChatMessage.builder()
                .chatId(chatId)
                .senderId(lukeId)
                .recipientId(obiWanId)
                .senderName("Luke")
                .recipientName("Obi Wan")
                .content("What's up old friend?")
                .timestamp(Date.from(Instant.now().plusSeconds(2)))
                .status(MessageStatus.DELIVERED)
                .build();
    }

    @AfterEach
    void cleanup() {
        chatMessageRepository.deleteAll();
    }

    @Test
    void givenSavedMessages_whenCountedBySenderIdAndRecipientIdAndStatus_thenReturnCorrectCount() {

        // given
        chatMessageRepository.saveAll(List.of(messageToLuke, messageToObiWan1, messageToObiWan2));

        // when
        long messagesCount = chatMessageRepository.countBySenderIdAndRecipientIdAndStatus(lukeId, obiWanId, MessageStatus.DELIVERED);

        // then
        assertEquals(2, messagesCount);
    }

    @Test
    void givenSavedMessages_whenCountedWhileNotExisting_thenReturnZero() {

        // given
        chatMessageRepository.saveAll(List.of(messageToLuke, messageToObiWan1, messageToObiWan2));

        // when
        long messagesCount = chatMessageRepository.countBySenderIdAndRecipientIdAndStatus("notExistingId", obiWanId, MessageStatus.DELIVERED);

        // then
        assertEquals(0, messagesCount);
    }

    @Test
    void givenSavedMessages_whenSearchedByChatId_thenReturnListOfMatchingMessages() {

        // given
        chatMessageRepository.saveAll(List.of(messageToLuke, messageToObiWan1, messageToObiWan2));

        // when
        List<ChatMessage> listOfMessagesByChatId = chatMessageRepository.findByChatId(chatId);

        // then
        assertThat(listOfMessagesByChatId)
                .hasSize(3)
                .allMatch(chatMessage -> Objects.nonNull(chatMessage.getId()))
                .allMatch(chatMessage -> Objects.equals(chatMessage.getChatId(), chatId));
    }

    @Test
    void givenSavedMessages_whenDeletedById_thenRemoveMessageFromDb() {

        // given
        chatMessageRepository.saveAll(List.of(messageToLuke, messageToObiWan1, messageToObiWan2));
        String id = chatMessageRepository
                .findByChatId(chatId)
                .stream()
                .findAny()
                .map(ChatMessage::getId)
                .orElseThrow(() ->
                        new MessageNotFoundException("Could not find message")
                );

        // when
        chatMessageRepository.deleteById(id);

        // then
        Optional<ChatMessage> afterDelete = chatMessageRepository.findById(id);
        List<ChatMessage> listOfMessagesByChatId = chatMessageRepository.findByChatId(chatId);

        assertThat(afterDelete).isNotPresent();
        assertThat(listOfMessagesByChatId)
                .hasSize(2)
                .map(ChatMessage::getId)
                .doesNotContain(id);
    }
}

package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.chat.ChatRoom;
import com.construction_worker_forum_back.config.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;
    @InjectMocks
    private ChatRoomService chatRoomService;
    private final String senderId = "senderId";
    private final String recipientId = "recipientId";
    private final String chatId = String.format("%s_%s", senderId, recipientId);

    @Test
    void givenExistingChatRoomInDb_whenTryingToGetChatId_thenReturnChatId() {

        // given
        ChatRoom chatRoom = ChatRoom.builder()
                .id("should_be_generated")
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        given(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .willReturn(Optional.ofNullable(chatRoom));

        // when
        Optional<String> actualChatId = chatRoomService.getChatId(senderId, recipientId, true);

        // then
        assertTrue(actualChatId.isPresent());
        assertEquals(chatId, actualChatId.get());
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    void givenNotExistingChatRoom_whenTryingToGetChatId_thenCreateChatRoomsAndReturnCommonChatId() {

        // given
        given(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .willReturn(Optional.empty());

        // when
        Optional<String> actualChatId = chatRoomService.getChatId(senderId, recipientId, true);

        // then
        assertTrue(actualChatId.isPresent());
        assertEquals(chatId, actualChatId.get());
    }
}
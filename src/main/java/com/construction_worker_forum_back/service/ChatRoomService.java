package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.chat.ChatRoom;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.security.Role;
import com.construction_worker_forum_back.config.repository.ChatRoomRepository;
import com.construction_worker_forum_back.config.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExist) {

        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (!createIfNotExist) return Optional.empty();

                    var chatId = String.format("%s_%s", senderId, recipientId);

                    ChatRoom senderRecipient = ChatRoom.builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .recipientId(recipientId)
                            .build();

                    if (Objects.equals(senderId, recipientId)) {
                        chatRoomRepository.save(senderRecipient);
                        return Optional.of(chatId);
                    }

                    ChatRoom recipientSender = ChatRoom.builder()
                            .chatId(chatId)
                            .senderId(recipientId)
                            .recipientId(senderId)
                            .build();

                    chatRoomRepository.save(senderRecipient);
                    chatRoomRepository.save(recipientSender);
                    return Optional.of(chatId);
                });
    }

    public List<UserDto> findAllContacts() {
        return userRepository.findAll().stream()
                .filter(user -> user.getUserRoles().equals(Role.USER))
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }
}

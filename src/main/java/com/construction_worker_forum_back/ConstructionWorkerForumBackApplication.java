package com.construction_worker_forum_back;

import com.construction_worker_forum_back.model.chat.ChatRoom;
import com.construction_worker_forum_back.repository.ChatRoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EntityScan(basePackages = {"com.construction_worker_forum_back.model.entity"})
public class ConstructionWorkerForumBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConstructionWorkerForumBackApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(ChatRoomRepository chatRoomRepository) {
        return args -> {
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

            chatRoomRepository.save(senderRecipient);
            chatRoomRepository.save(recipientSender);
        };
    }
}

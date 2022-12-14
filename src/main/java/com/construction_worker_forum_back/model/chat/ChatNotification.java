package com.construction_worker_forum_back.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotification {
    private String id;
    private String senderId;
    private String senderName;
    private String content;

    public static ChatNotification of(String id, String senderId, String senderName, String content) {
        return new ChatNotification(id, senderId, senderName, content);
    }
}

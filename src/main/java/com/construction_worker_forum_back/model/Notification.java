package com.construction_worker_forum_back.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private String from;
    private String to;
    private String message;
    private String redirectTo;

    public static Notification of(String senderUsername, String recipientId, String message, String redirect) {
        return new Notification(senderUsername, recipientId, message, redirect);
    }
}

package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.service.ChatRoomService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@CrossOrigin("http://localhost:3000")
public class ContactsController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/users/summaries")
    public ResponseEntity<?> findAllContacts() {
        return ResponseEntity.ok(chatRoomService.findAllContacts());
    }
}

package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.service.ChatRoomService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("https://localhost:3000")
public class ContactsController {

    private final ChatRoomService chatRoomService;

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/users/summaries")
    public ResponseEntity<List<UserDto>> findAllContacts() {
        return ResponseEntity.ok(chatRoomService.findAllContacts());
    }
}

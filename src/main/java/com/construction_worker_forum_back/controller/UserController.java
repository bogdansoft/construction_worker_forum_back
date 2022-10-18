package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.DTOs.UserRequest;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    ModelMapper modelMapper;
    UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(@Valid @RequestBody UserRequest userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        return userService.register(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @GetMapping("/{id}")
    User getUser(@PathVariable Long id) {
        return userService.getUser(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    Map<String, String> deleteUser(@PathVariable Long id) {
        if(userService.deleteUser(id)) {
            return Map.of(
                    "ID", id+"",
                    "status", "Deleted successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    User updateUser(@Valid @RequestBody UserRequest userRequest, @PathVariable Long id) {
        return userService.updateUser(id, userRequest);
    }
}
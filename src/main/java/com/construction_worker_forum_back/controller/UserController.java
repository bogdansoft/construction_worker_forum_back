package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
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
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    ModelMapper modelMapper;
    UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userService
                .register(userRequestDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @GetMapping("/{id}")
    UserDto getUser(@PathVariable Long id) {
        return userService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    Map<String, String> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return Map.of(
                    "ID", id + "",
                    "status", "Deleted successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    UserDto updateUser(@Valid @RequestBody UserRequestDto userRequestDto, @PathVariable Long id) {
        return userService.updateUser(id, userRequestDto);
    }
}

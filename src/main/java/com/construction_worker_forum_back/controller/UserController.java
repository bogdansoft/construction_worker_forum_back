package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/user")
@Tag(name = "User", description = "The User API. Contains all the operations that can be performed on a user.")
@AllArgsConstructor
public class UserController {
    UserService userService;

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/all")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Delete user", description = "Delete user")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    UserDto getUser(@PathVariable Long id) {
        return userService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    UserDto getUserByUsername(@RequestParam(value = "username") String username) {
        return userService
                .findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userService
                .register(userRequestDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/{username}/changebio")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto changeBio(@PathVariable String username, @Valid @RequestBody String newBio) {
        return userService.changeBio(username, newBio);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    UserDto updateUser(@Valid @RequestBody UserRequestDto userRequestDto, @PathVariable Long id) {
        return userService.updateUser(id, userRequestDto);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'USER')")
    @SecurityRequirement(name = "Bearer Authentication")
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
}

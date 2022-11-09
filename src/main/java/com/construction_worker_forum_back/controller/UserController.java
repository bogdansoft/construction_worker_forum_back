package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.model.dto.simple.BioSimpleDto;
import com.construction_worker_forum_back.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping(path="/api/user")
@Tag(name = "User", description = "The User API. Contains all the operations that can be performed on a user.")
@AllArgsConstructor
public class UserController {
    UserService userService;

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    UserDto getUser(@PathVariable Long id) {
        return userService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/user")
    UserDto getUserByUsername(@RequestParam() String username) {
        return userService
                .findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userService
                .register(userRequestDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{username}/bio")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto changeBio(@PathVariable String username, @Valid @RequestBody BioSimpleDto newBio) {
        return userService.changeBio(username, newBio);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping( path="/changeavatar", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    public byte[] changeAvatar(@RequestParam(value = "file") MultipartFile multipartFile, @RequestParam("username") String username) throws IOException {

        return userService.changeAvatar(username, multipartFile);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    UserDto updateUser(@Valid @RequestBody UserRequestDto userRequestDto, @PathVariable Long id) {
        return userService.updateUser(id, userRequestDto);
    }

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

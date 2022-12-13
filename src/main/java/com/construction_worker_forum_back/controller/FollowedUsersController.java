package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import com.construction_worker_forum_back.service.FollowedUsersService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("https://localhost:3000")
@RequestMapping("/api/following")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Post", description = "The Followed Users API. Contains all the operations that can be performed on a followed users.")
@AllArgsConstructor
public class FollowedUsersController {

    private final FollowedUsersService followedUsersService;

    @GetMapping("/followed/{id}")
    public List<UserSimpleDto> getFollowedUsersByUserId(@PathVariable(name = "id") Long id) {
        return followedUsersService.getFollowedUsersByUserId(id);
    }

    @GetMapping("/followers/{id}")
    public List<UserSimpleDto> getFollowersByUserId(@PathVariable(name = "id") Long id) {
        return followedUsersService.getFollowersByUserId(id);
    }

    @PostMapping("/{id}")
    public UserSimpleDto followUser(
            @PathVariable(name = "id") Long followedUserId,
            @RequestParam(name = "followerId") Long followerId) {
        return followedUsersService.createFollowedUser(followedUserId, followerId);
    }

    @DeleteMapping("/{id}")
    Map<String, String> unfollowUser(@PathVariable(name = "id") Long followedUserId,
                                     @RequestParam(name = "followerId") Long followerId) {
        if (followedUsersService.unfollowUser(followedUserId, followerId)) {
            return Map.of(
                    "Follower ID: ", followerId + "",
                    "Followed ID: ", followedUserId + "",
                    "status", "Unfollowed successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}

package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import com.construction_worker_forum_back.service.FollowedUserService;
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
public class FollowedUserController {

    private final FollowedUserService followedUserService;

    @GetMapping("/followed/{username}")
    public List<UserSimpleDto> getFollowedUsersByUsername(@PathVariable(name = "username") String username) {
        return followedUserService.getFollowedUsersByUsername(username);
    }

    @GetMapping("/followers/{username}")
    public List<UserSimpleDto> getFollowersByUsername(@PathVariable(name = "username") String username) {
        return followedUserService.getFollowersByUsername(username);
    }

    @GetMapping("/{username}")
    public boolean isUserFollowed(
            @PathVariable(name = "username") String username,
            @RequestParam(name = "followerId") Long followerId) {
        return followedUserService.isUserFollowedByUserWithId(username, followerId);
    }

    @PostMapping("/{username}")
    public UserSimpleDto followUser(
            @PathVariable(name = "username") String followedUserUsername,
            @RequestParam(name = "followerId") Long followerId) {
        return followedUserService.createFollowedUser(followedUserUsername, followerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @DeleteMapping("/{username}")
    Map<String, String> unfollowUser(@PathVariable(name = "username") String followedUserUsername,
                                     @RequestParam(name = "followerId") Long followerId) {
        if (followedUserService.unfollowUser(followedUserUsername, followerId)) {
            return Map.of(
                    "Follower ID: ", followerId + "",
                    "Followed username: ", followedUserUsername + "",
                    "status", "Unfollowed successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}

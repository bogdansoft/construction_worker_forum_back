package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.entity.FollowedUsers;
import com.construction_worker_forum_back.service.FollowedUsersService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("https://localhost:3000")
@RequestMapping("/api/followed_users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Post", description = "The Followed Users API. Contains all the operations that can be performed on a followed users.")
@AllArgsConstructor
public class FollowedUsersController {

    private final FollowedUsersService followedUsersService;

//    @GetMapping("/{id}")
//    public List<FollowedUsers> getFollowedUsersByUserId(@PathVariable(name = "id") Long id) {
//        //User simpler dto to return
//        List<FollowedUsers> list = followedUsersService.getFollowedUsersByUserId(id);
//        return list;
//    }
//
//    @PostMapping("/{id}")
//    public FollowedUsers createFollowedUsers(
//            @PathVariable(name = "id") Long followedUserId,
//            @RequestParam(name = "followerId") Long followerId) {
//        FollowedUsers followedUsers = followedUsersService.createFollowedUser(followedUserId, followerId);
//        System.out.println(followedUsers);
//        return followedUsers;
//    }
}

package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.entity.FollowedUsers;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.FollowedUsersRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowedUsersService {

    private final FollowedUsersRepository followedUsersRepository;
    private final UserRepository userRepository;

//    public List<FollowedUsers> getFollowedUsersByUserId(Long id) {
//        return followedUsersRepository.findAllByUserId(id);
//    }

//    public FollowedUsers createFollowedUser(Long followedUserId, Long followerId) {
//        Optional<User> followingUser = userRepository.findById(followerId);
//        String followedUserUsername = userRepository.findUsernameByUser_id(followedUserId);
//
//        return followedUsersRepository.save(
//                FollowedUsers.builder()
//                .followedUserId(followedUserId)
//                .username(followedUserUsername)
//                .user(followingUser.get())
//                .build());
//    }
}

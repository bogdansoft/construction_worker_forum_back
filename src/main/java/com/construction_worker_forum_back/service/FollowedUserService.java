package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import com.construction_worker_forum_back.model.entity.FollowedUser;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.FollowedUserRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowedUserService {

    private final FollowedUserRepository followedUserRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public List<UserSimpleDto> getFollowedUsersByUsername(String username) {
        return followedUserRepository.findAllFollowedUsersByUsername(username)
                .stream()
                .map(user -> modelMapper.map(user.getFollowedUsers(), UserSimpleDto.class))
                .filter(user -> !user.getAccountStatus().toString().equals("DELETED"))
                .toList();
    }

    public List<UserSimpleDto> getFollowersByUsername(String username) {
        return followedUserRepository.findAllFollowersByUsername(username)
                .stream()
                .map(user -> modelMapper.map(user.getFollowingUser(), UserSimpleDto.class))
                .filter(user -> !user.getAccountStatus().toString().equals("DELETED"))
                .toList();
    }

    public Boolean isUserFollowedByUserWithId(String followedUserUsername, Long followingUserId) {
        return followedUserRepository.findByFollowedUserUsernameAndFollowerId(followedUserUsername, followingUserId).isPresent();
    }

    @Transactional
    public Optional<UserSimpleDto> createFollowedUser(String followedUserUsername, Long followerId) {
        Optional<FollowedUser> existFollowing = followedUserRepository.findByFollowedUserUsernameAndFollowerId(followedUserUsername, followerId);
        if (existFollowing.isPresent()) return Optional.empty();
        User followedUser = userRepository.findByUsername(followedUserUsername).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User followingUser = userRepository.findById(followerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        followedUserRepository.save(
                FollowedUser.builder()
                        .followedUsers(followedUser)
                        .followingUser(followingUser)
                        .build());
        return Optional.of(modelMapper.map(followedUser, UserSimpleDto.class));
    }

    @Transactional
    public boolean unfollowUser(String followedUserUsername, Long followerId) {
        Optional<FollowedUser> followedUser = followedUserRepository.findByFollowedUserUsernameAndFollowerId(followedUserUsername, followerId);
        if (followedUser.isEmpty()) return false;
        return followedUserRepository.deleteByFollowedUsers_UsernameAndFollowingUser_Id(followedUserUsername, followerId) == 1;
    }
}

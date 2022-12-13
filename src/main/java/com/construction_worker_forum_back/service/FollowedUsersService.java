package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import com.construction_worker_forum_back.model.entity.FollowedUser;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.FollowedUsersRepository;
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
public class FollowedUsersService {

    private final FollowedUsersRepository followedUsersRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public List<UserSimpleDto> getFollowedUsersByUserId(Long id) {
        return followedUsersRepository.findAllFollowedUsersByUserId(id)
                .stream()
                .map(user -> modelMapper.map(user.getFollowedUsers(), UserSimpleDto.class))
                .toList();
    }

    public List<UserSimpleDto> getFollowersByUserId(Long id) {
        return followedUsersRepository.findAllFollowersByUserId(id)
                .stream()
                .map(user -> modelMapper.map(user.getFollowingUser(), UserSimpleDto.class))
                .toList();
    }

    @Transactional
    public UserSimpleDto createFollowedUser(Long followedUserId, Long followerId) {
        User followedUser = userRepository.findById(followedUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User followingUser = userRepository.findById(followerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        followedUsersRepository.save(
                FollowedUser.builder()
                        .followedUsers(followedUser)
                        .followingUser(followingUser)
                        .build());
        return modelMapper.map(followedUser, UserSimpleDto.class);
    }

    @Transactional
    public boolean unfollowUser(Long followedUserId, Long followerId) {
        Optional<FollowedUser> followedUser = followedUsersRepository.findByFollowedUsersIdAndFollowerId(followedUserId, followerId);
        if (followedUser.isEmpty()) return false;
        return followedUsersRepository.deleteByFollowedUsers_IdAndFollowingUser_Id(followedUserId, followerId) == 1;
    }
}

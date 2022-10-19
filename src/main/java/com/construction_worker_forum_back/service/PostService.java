package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public List<PostDto> getAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    public PostDto createPost(PostRequestDto post) {
        Post postToSave = new Post();
        UserDto userById = userService
                .findById(post.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        postToSave.setUser(modelMapper.map(userById, User.class));
        postToSave.setCreatedAt(Date.from(Instant.now()));
        postToSave.setContent(post.getContent());
        postToSave.setTitle(post.getTitle());
        return modelMapper.map(postRepository.save(postToSave), PostDto.class);
    }

    public Boolean deleteById(Long id) {
        return postRepository.deletePostById(id) == 1;
    }

    public PostDto updatePostById(Long id, PostRequestDto post) {
        Post postFromDb = postRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        postFromDb.setTitle(post.getTitle());
        postFromDb.setContent(post.getContent());
        postFromDb.setUpdatedAt(Date.from(Instant.now()));
        return modelMapper.map(postRepository.save(postFromDb), PostDto.class);
    }

    public Optional<PostDto> findById(Long id) {
        return postRepository.findById(id)
                .map(user -> modelMapper.map(user, PostDto.class));
    }
}

package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public List<PostDto> getAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    public Optional<PostDto> findById(Long id) {
        return postRepository.findById(id)
                .map(post -> modelMapper.map(post, PostDto.class));
    }

    @Transactional
    public PostDto createPost(PostRequestDto postRequestDto) {
        Post postToSave = modelMapper.map(postRequestDto, Post.class);
        /*UserDto userById = userService
                .findById(postRequestDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
*/
        return modelMapper.map(postRepository.save(postToSave), PostDto.class);
    }

    @Transactional
    public PostDto updatePostById(Long id, PostRequestDto postRequestDto) {
        Post postFromDb = postRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        modelMapper.map(postRequestDto, postFromDb);
        postFromDb.setUpdatedAt(Date.from(Instant.now()));

        return modelMapper.map(postFromDb, PostDto.class);
    }

    @Transactional
    public boolean deleteById(Long id) {
        return postRepository.deletePostById(id) == 1;
    }

}

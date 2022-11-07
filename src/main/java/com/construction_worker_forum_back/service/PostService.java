package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.dto.TopicDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.simple.LikerSimpleDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.Topic;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.PostRepository;
import com.construction_worker_forum_back.repository.UserRepository;
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

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TopicService topicService;
    private final ModelMapper modelMapper;

    public List<PostDto> getAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    public List<PostDto> getPostsByUsername(String username) {
        return postRepository
                .findByUser_UsernameIgnoreCase(username)
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    public List<PostDto> getPostsByTopicId(Long topicId) {
        return postRepository
                .findByTopic_Id(topicId)
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    public List<LikerSimpleDto> getPostLikers(Long id) {
        return postRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getLikers()
                .stream()
                .map(user -> modelMapper.map(user, LikerSimpleDto.class))
                .toList();
    }

    public Optional<PostDto> findById(Long id) {
        return postRepository.findById(id)
                .map(post -> modelMapper.map(post, PostDto.class));
    }

    @Transactional
    public PostDto createPost(PostRequestDto postRequestDto) {
        Post postToSave = modelMapper.map(postRequestDto, Post.class);
        UserDto userById = userService
                .findById(postRequestDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TopicDto topicById = topicService
                .findTopicById(postRequestDto.getTopicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        postToSave.setUser(modelMapper.map(userById, User.class));
        postToSave.setTopic(modelMapper.map(topicById, Topic.class));

        return modelMapper.map(postRepository.save(postToSave), PostDto.class);
    }

    @Transactional
    public PostDto likePost(Long postId, Long userId) {
        Post postFromDb = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User userById = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (postFromDb.getLikers().contains(userById)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Post already liked by user!");
        }

        postFromDb.getLikers().add(userById);
        userById.getLikedPosts().add(postFromDb);

        return modelMapper.map(postFromDb, PostDto.class);
    }

    @Transactional
    public PostDto updatePostById(Long id, PostRequestDto postRequestDto) {
        Post postFromDb = postRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        TopicDto topicById = topicService
                .findTopicById(postRequestDto.getTopicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        modelMapper.map(postRequestDto, postFromDb);
        modelMapper.map(postRequestDto, topicById);
        postFromDb.setUpdatedAt(Date.from(Instant.now()));
        postFromDb.setTopic(modelMapper.map(topicById, Topic.class));

        return modelMapper.map(postFromDb, PostDto.class);
    }

    @Transactional
    public boolean deleteById(Long id) {
        return postRepository.deletePostById(id) == 1;
    }

    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        Post postFromDb = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User userById = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return postFromDb.getLikers().remove(userById) && userById.getLikedPosts().remove(postFromDb);
    }

    public List<PostDto> findPostByContentOrTitle(String contentOrTitle) {
        return postRepository.findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(contentOrTitle, contentOrTitle)
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();

    }
}

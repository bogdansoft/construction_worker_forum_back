package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.client.NotificationClient;
import com.construction_worker_forum_back.model.Notification;
import com.construction_worker_forum_back.model.dto.CommentDto;
import com.construction_worker_forum_back.model.dto.CommentRequestDto;
import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.simple.LikerSimpleDto;
import com.construction_worker_forum_back.model.entity.Comment;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.CommentRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final NotificationClient notificationClient;

    public List<CommentDto> getAllComments() {
        return commentRepository
                .findAll()
                .stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .toList();
    }

    public List<CommentDto> getCommentsByUsername(String username) {
        return commentRepository
                .findByUser_UsernameIgnoreCase(username)
                .stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .toList();
    }

    public List<LikerSimpleDto> getCommentLikers(Long id) {
        return commentRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getLikers()
                .stream()
                .map(user -> modelMapper.map(user, LikerSimpleDto.class))
                .toList();
    }

    @Cacheable(value = "commentCache", key = "{#id}")
    public Optional<CommentDto> findById(Long id) {
        return commentRepository.findById(id)
                .map(comment -> modelMapper.map(comment, CommentDto.class));
    }

    @Transactional
    public CommentDto createComment(CommentRequestDto commentRequestDto, Long commentForReplyId) {
        Comment commentToSave = modelMapper.map(commentRequestDto, Comment.class);

        UserDto userById = userService
                .findById(commentRequestDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        PostDto postById = postService
                .findById(commentRequestDto.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        commentToSave.setUser(modelMapper.map(userById, User.class));
        commentToSave.setPost(modelMapper.map(postById, Post.class));

        notificationClient.sendNotification(
                        Notification.of(
                                userById.getUsername(),
                                postById.getUser().getId().toString(),
                                "Commented on your post!",
                                "/post/" + commentRequestDto.getPostId(),
                                false
                        )
                )
                .doOnNext(notification -> log.info("Notification Response: {}", notification))
                .doOnError(e -> log.info("Error occurred: {}", e.getMessage()))
                .subscribe();

        if (commentForReplyId != null) {
            Comment commentForReplyById = commentRepository.findById(commentForReplyId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            commentToSave.setParentComment(commentForReplyById);
            Comment savedComment = commentRepository.save(commentToSave);
            commentForReplyById.getSubComments().add(savedComment);
            commentRepository.save(commentForReplyById);

            return modelMapper.map(savedComment, CommentDto.class);
        }

        return modelMapper.map(commentRepository.save(commentToSave), CommentDto.class);
    }

    @Transactional
    @CachePut(value = "commentCache", key = "{#commentId}")
    public CommentDto likeComment(Long commentId, Long userId) {
        Comment commentFromDb = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User userById = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (commentFromDb.getLikers().contains(userById)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Comment already liked by user!");
        }

        commentFromDb.getLikers().add(userById);
        userById.getLikedComments().add(commentFromDb);

        return modelMapper.map(commentFromDb, CommentDto.class);
    }

    @Transactional
    @CachePut(value = "commentCache", key = "{#id}")
    public CommentDto updateCommentById(Long id, CommentRequestDto commentRequestDto) {
        Comment comment = commentRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        modelMapper.map(commentRequestDto, comment);
        comment.setUpdatedAt(Date.from(Instant.now()));

        return modelMapper.map(comment, CommentDto.class);
    }

    @Transactional
    @CacheEvict(value = "commentCache", key = "{#commentId}")
    public boolean deleteById(Long commentId, Long userId) {
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User owner = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!(Objects.equals(comment.getUser().getId(), owner.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        for (User usersWhoLikedIt : comment.getLikers()) {
            usersWhoLikedIt.getLikedComments().remove(comment);
        }
        for (Comment subComment : comment.getSubComments()) {
            subComment.setParentComment(null);
            for (User usersWhoLikedSubComment : subComment.getLikers()) {
                usersWhoLikedSubComment.getLikedComments().remove(subComment);
            }
            subComment.getLikers().clear();
        }
        return commentRepository.deleteCommentById(commentId) == 1;
    }

    @Transactional
    @CachePut(value = "commentCache", key = "{#commentId}")
    public CommentDto unlikeComment(Long commentId, Long userId) {
        Comment commentFromDb = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User userById = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        commentFromDb.getLikers().remove(userById);
        userById.getLikedComments().remove(commentFromDb);

        return modelMapper.map(commentFromDb, CommentDto.class);
    }

    public List<CommentDto> getCommentsOfPost(Long id) {
        return commentRepository.findByPost_Id(id)
                .stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .toList();
    }

    public List<CommentDto> getCommentsOfParentComment(Long id) {
        return commentRepository.findByParentComment_Id(id)
                .stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .toList();
    }
}
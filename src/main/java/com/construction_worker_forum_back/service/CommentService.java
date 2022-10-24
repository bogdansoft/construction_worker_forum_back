package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.CommentDto;
import com.construction_worker_forum_back.model.dto.CommentRequestDto;
import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.entity.Comment;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.CommentRepository;
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
public class CommentService {

    CommentRepository commentRepository;
    PostService postService;
    UserService userService;
    ModelMapper modelMapper;

    public List<CommentDto> getAllComments() {
        return commentRepository
                .findAll()
                .stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .toList();
    }

    public Optional<CommentDto> findById(Long id) {
        return commentRepository.findById(id)
                .map(comment -> modelMapper.map(comment, CommentDto.class));
    }

    @Transactional
    public CommentDto createComment(CommentRequestDto commentRequestDto) {
        Comment commentToSave = modelMapper.map(commentRequestDto, Comment.class);

        UserDto userById = userService
                .findById(commentRequestDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        PostDto postById = postService
                .findById(commentRequestDto.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        commentToSave.setUser(modelMapper.map(userById, User.class));
        commentToSave.setPost(modelMapper.map(postById, Post.class));
        return modelMapper.map(commentRepository.save(commentToSave), CommentDto.class);
    }

    @Transactional
    public CommentDto updateCommentById(Long id, CommentRequestDto commentRequestDto) {
        Comment comment = commentRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        modelMapper.map(commentRequestDto, comment);
        comment.setUpdatedAt(Date.from(Instant.now()));

        return modelMapper.map(comment, CommentDto.class);
    }

    @Transactional
    public boolean deleteById(Long id) {
        return commentRepository.deleteCommentById(id) == 1;
    }

    public List<CommentDto> getCommentsOfPost(Long id) {
        return commentRepository.findByPost_Id(id)
                .stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .toList();
    }
}

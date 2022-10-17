package com.construction_worker_forum_back.controller;


import com.construction_worker_forum_back.model.DTOs.CommentRequest;
import com.construction_worker_forum_back.model.DTOs.UserRequest;
import com.construction_worker_forum_back.model.entity.Comment;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.service.CommentService;
import com.construction_worker_forum_back.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CommentService commentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    Comment createComment(@Valid @RequestBody CommentRequest commentRequest) {
        Comment comment = modelMapper.map(commentRequest, Comment.class);
        return commentService.register(comment)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
    }

    @GetMapping("/{id}")
    Comment getComment(@PathVariable Long id) {
        return commentService.getComment(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    List<Comment> getAllComments() {
        return commentService.getAllComments();
    }

    @DeleteMapping("/{id}")
    String deleteComment(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }

    @PutMapping("/{id}")
    Comment updateComment(@Valid @RequestBody CommentRequest userRequest, @PathVariable Long id) {
        return commentService.updateComment(id, userRequest);
    }



}

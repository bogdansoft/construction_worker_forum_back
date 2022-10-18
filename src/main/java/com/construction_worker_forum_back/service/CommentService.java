package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.DTOs.CommentRequest;
import com.construction_worker_forum_back.model.DTOs.UserRequest;
import com.construction_worker_forum_back.model.entity.*;
import com.construction_worker_forum_back.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Transactional
    public Optional<Comment> register(Comment comment) {
        comment.setCreatedAt(Date.from(Instant.now()));
        return Optional.of(commentRepository.save(comment));
    }

    public Optional<Comment> getComment(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> getAllComments() {
        List<Comment> allComments = new ArrayList<>();
        commentRepository.findAll().forEach(allComments::add);
        return allComments;
    }

    @Transactional
    public String deleteComment(Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return "SUCCESS";
        } else {
            return "FAIL";
        }
    }

    @Transactional
    public Comment updateComment(Long id, CommentRequest commentRequest) {
        if(!commentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Comment comment = commentRepository.findById(id).get();
        comment.setContent(commentRequest.getContent());
        comment.setUpdatedAt(Date.from(Instant.now()));

        return commentRepository.save(comment);
    }

}

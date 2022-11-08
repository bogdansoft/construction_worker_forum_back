package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.CommentDto;
import com.construction_worker_forum_back.model.dto.CommentRequestDto;
import com.construction_worker_forum_back.model.dto.simple.LikerSimpleDto;
import com.construction_worker_forum_back.service.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/comment")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Comment", description = "The Comment API. Contains all the operations that can be performed on a comment.")
@AllArgsConstructor
public class CommentController {
    CommentService commentService;

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    @GetMapping
    List<CommentDto> getAllComments() {
        return commentService.getAllComments();
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/all_by_username/{username}")
    public List<CommentDto> getAllCommentsByUsername(@PathVariable String username) {
        return commentService.getCommentsByUsername(username);
    }

    @GetMapping("/{id}")
    CommentDto getComment(@PathVariable Long id) {
        return commentService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/likers/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<LikerSimpleDto> getCommentLikers(@PathVariable("id") Long id) {
        return commentService.getCommentLikers(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto createComment(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info(String.valueOf(commentRequestDto));
        return commentService.createComment(commentRequestDto);
    }

    @PostMapping("/like")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto likeComment(@RequestParam Long commentId, @RequestParam Long userId) {
        return commentService.likeComment(commentId, userId);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    CommentDto updateComment(@Valid @RequestBody CommentRequestDto userRequest, @PathVariable Long id) {
        return commentService.updateCommentById(id, userRequest);
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{commentId}")
    public Boolean deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
       return commentService.deleteById(commentId, userId);
    }

    @GetMapping("/post/{id}")
    List<CommentDto> getCommentsOfPost(@PathVariable Long id) {
        return commentService.getCommentsOfPost(id);
    }
}

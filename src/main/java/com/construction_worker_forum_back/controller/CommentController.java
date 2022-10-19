package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.CommentDto;
import com.construction_worker_forum_back.model.dto.CommentRequestDto;
import com.construction_worker_forum_back.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentController {
    CommentService commentService;

    @GetMapping
    List<CommentDto> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/{id}")
    CommentDto getComment(@PathVariable Long id) {
        return commentService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto createComment(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.createComment(commentRequestDto);
    }

    @PutMapping("/{id}")
    CommentDto updateComment(@Valid @RequestBody CommentRequestDto userRequest, @PathVariable Long id) {
        return commentService.updateCommentById(id, userRequest);
    }

    @DeleteMapping("/{id}")
    Map<String, String> deleteComment(@PathVariable Long id) {
        if (commentService.deleteById(id)) {
            return Map.of(
                    "ID", id + "",
                    "status", "Deleted successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}

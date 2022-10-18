package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.DTOs.PostRequest;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {

    private PostService postService;

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    @GetMapping("/list")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable("id") Long id) {
        return postService.getPostById(id);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@Valid @RequestBody PostRequest post) {
        return postService.createPost(post);
    }

    @DeleteMapping("/{id}")
    public String deletePostById(@PathVariable("id") Long id) {
        return postService.deletePostById(id);
    }

    @PutMapping("/{id}")
    public Post updatePostById(@PathVariable("id") Long id, @RequestBody PostRequest post) {
        return postService.updatePostById(id, post);
    }
}

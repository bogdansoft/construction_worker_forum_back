package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.DTOs.PostDTO;
import com.construction_worker_forum_back.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api")
@AllArgsConstructor
public class PostController {

    private PostService postService;

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/post/{id}")
    public Post getPostById(@PathVariable("id") Long id) {
        return postService.getPostById(id);
    }

    @PostMapping("/post/add")
    public Post createPost(@RequestBody PostDTO post) {
        return postService.createPost(post);
    }

    @DeleteMapping("/post/{id}")
    public String deletePostById(@PathVariable("id") Long id) {
        return postService.deletePostById(id);
    }

    @PostMapping("/post/{id}")
    public Post updatePostById(@PathVariable("id") Long id, @RequestBody PostDTO post) {
        return postService.updatePostById(id, post);
    }
}

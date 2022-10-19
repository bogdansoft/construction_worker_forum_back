package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@Valid @RequestBody PostRequestDto post) {
        return postService.createPost(post);
    }

    @DeleteMapping("/post/{id}")
    public String deletePostById(@PathVariable("id") Long id) {
        return postService.deletePostById(id);
    }

    @PostMapping("/post/{id}")
    public Post updatePostById(@PathVariable("id") Long id, @RequestBody PostRequestDto post) {
        return postService.updatePostById(id, post);
    }
}

package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.dto.simple.LikerSimpleDto;
import com.construction_worker_forum_back.service.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("https://localhost:3000")
@RequestMapping("/api/post")
@Tag(name = "Post", description = "The Post API. Contains all the operations that can be performed on a post.")
@AllArgsConstructor
public class PostController {

    private PostService postService;

    @GetMapping
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/all_by_username/{username}")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<PostDto> getAllPostsByUsername(@PathVariable String username) {
        return postService.getPostsByUsername(username);
    }

    @GetMapping("/all_by_topicid/{topicId}")
    public List<PostDto> getAllPostsByTopicId(
            @PathVariable Long topicId,
            @RequestParam(name = "orderby") Optional<String> orderBy,
            @RequestParam(name = "limit") Optional<Integer> limit,
            @RequestParam(name = "page") Optional<Integer> page,
            @RequestParam(name = "keywords", required = false) List<String> allParams
    ) {
        return postService.getPostsByTopicId(topicId, orderBy, limit, page, allParams);
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable("id") Long id) {
        return postService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/likers/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<LikerSimpleDto> getPostLikers(@PathVariable("id") Long id) {
        return postService.getPostLikers(id);
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createPost(@Valid @RequestBody PostRequestDto post) {
        return postService.createPost(post);
    }

    @PostMapping("/like")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto likePost(@RequestParam Long postId, @RequestParam Long userId) {
        return postService.likePost(postId, userId);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public PostDto updatePostById(@PathVariable("id") Long id, @RequestBody PostRequestDto post) {
        return postService.updatePostById(id, post);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Map<String, String> deletePostById(@PathVariable("id") Long id) {
        if (postService.deleteById(id)) {
            return Map.of(
                    "ID", id + "",
                    "status", "Deleted successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/like")
    @SecurityRequirement(name = "Bearer Authentication")
    public Map<String, String> unlikePost(@RequestParam Long postId, @RequestParam Long userId) {
        if (postService.unlikePost(postId, userId)) {
            return Map.of(
                    "Post ID", postId + "",
                    "status", "Post unliked successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public List<PostDto> findPostByContentOrTitle(@RequestParam(name ="searchItem") String contentOrTitle){
        System.out.println(postService.findPostByContentOrTitle(contentOrTitle));
        return postService.findPostByContentOrTitle(contentOrTitle);
    }
}

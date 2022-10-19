package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        List<Post> allPosts = new ArrayList<>();
        postRepository.findAll().forEach(allPosts::add);
        return allPosts;
    }

    public Post createPost(PostRequestDto post) {
        Post postToSave = new Post();
        postToSave.setCreatedAt(Date.from(Instant.now()));
        postToSave.setContent(post.getContent());
        postToSave.setTitle(post.getTitle());
        return postRepository.save(postToSave);
    }

    public String deletePostById(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return "SUCCESS";
        } else {
            return "FAIL";
        }
    }

    public Post updatePostById(Long id, PostRequestDto post) {
        if (postRepository.findById(id).isPresent()) {
            Post postPrev = postRepository.findById(id).get();
            postPrev.setTitle(post.getTitle());
            postPrev.setContent(post.getContent());
            postPrev.setUpdatedAt(Date.from(Instant.now()));
            postRepository.save(postPrev);
        }
        log.warn("No such post found when updating by id");
        return null;
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow();
    }
}

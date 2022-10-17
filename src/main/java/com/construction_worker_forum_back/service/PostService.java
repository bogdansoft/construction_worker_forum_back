package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.entity.Post;
import com.construction_worker_forum_back.entity.PostDTO;
import com.construction_worker_forum_back.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

    public Post createPost(PostDTO post) {
        Post postToSave = new Post();
        postToSave.setCreatedAt(Date.valueOf(LocalDate.now()));
        postToSave.setContent(post.getContent());
        postToSave.setTitle(post.getTitle());
        return postRepository.save(postToSave);
    }

    public String deletePostById(Long id) {
        if(postRepository.existsById(id)){
            postRepository.deleteById(id);
            return "SUCCESS";
        }
        else {
            return "No such id found";
        }
    }

    public Post updatePostById(Long id, PostDTO post) {
        if(postRepository.findById(id).isPresent()) {
            Post postPrev = postRepository.findById(id).get();
            postPrev.setTitle(post.getTitle());
            postPrev.setContent(post.getContent());
            postPrev.setUpdatedAt(Date.valueOf(LocalDate.now()));
            postRepository.save(postPrev);
        }
        log.warn("No such post found when updating by id");
        return null;
    }
}

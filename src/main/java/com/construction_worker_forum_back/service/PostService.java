package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.entity.Post;
import com.construction_worker_forum_back.entity.PostDTO;
import com.construction_worker_forum_back.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
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
}

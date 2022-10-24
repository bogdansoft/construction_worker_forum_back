package com.construction_worker_forum_back.repository;

import com.construction_worker_forum_back.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    int deletePostById(Long postId);

    @Query("SELECT p FROM Post p  JOIN FETCH p.comments c")
    List<Post> findAllWithComments();
}

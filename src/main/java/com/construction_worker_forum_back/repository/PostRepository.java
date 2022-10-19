package com.construction_worker_forum_back.repository;

import com.construction_worker_forum_back.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    int deletePostById(Long postId);
}

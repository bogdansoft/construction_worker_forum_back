package com.construction_worker_forum_back.repository;

import com.construction_worker_forum_back.entity.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {

}

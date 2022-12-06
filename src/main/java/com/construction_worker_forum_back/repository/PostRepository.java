package com.construction_worker_forum_back.repository;

import com.construction_worker_forum_back.model.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUser_UsernameIgnoreCase(@NonNull String username);

    List<Post> findByTopic_Id(@NonNull Long id);

    int deletePostById(Long postId);

    List<Post> findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(String title, String content);

    List<Post> findAllPaginatedByTopic_Id(Long id, Pageable pageable);

    @Query(
            value = "select p from Post p left join fetch p.keywords k where p.topic.id = :topicId and k.name in :keywords"
    )
    List<Post> findAllPostsByTopicIdAndKeywords(Long topicId, Set<String> keywords);

    @Query(
            value = "select p from Post p left join fetch p.keywords k where p.topic.id = :topicId and k.name in :keywords"
    )
    List<Post> findAllSortedPostsByTopicIdAndKeywords(Long topicId, Set<String> keywords, Sort sort);
}

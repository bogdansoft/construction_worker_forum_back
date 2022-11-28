package com.construction_worker_forum_back.repository;

import com.construction_worker_forum_back.model.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    int deleteTopicById(Long id);

    Optional<Topic> findTopicByName(String name);

    List<Topic> findByNameContainsIgnoreCase(String name);

    @Query(
            value = "SELECT * FROM topics LIMIT ?1 OFFSET ?2",
            nativeQuery = true
    )
    List<Topic> getDesignatedNumberOfTopics(Integer paginationNumber, Integer index);
}

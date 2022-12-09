package com.construction_worker_forum_back.repository;

import com.construction_worker_forum_back.model.entity.FollowedUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowedUsersRepository extends JpaRepository<FollowedUsers, Long> {
//
//    @Query("select f from FollowedUsers f where f.user.id = ?1")
//    List<FollowedUsers> findAllByUserId(Long id);
}

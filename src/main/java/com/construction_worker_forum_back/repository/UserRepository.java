package com.construction_worker_forum_back.repository;

import com.construction_worker_forum_back.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameIgnoreCase(String username);

    int deleteByUsernameIgnoreCase(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByUsername(String username);

    @Query("select u.username from User u where u.id = ?1")
    String findUsernameByUser_id(Long id);
}

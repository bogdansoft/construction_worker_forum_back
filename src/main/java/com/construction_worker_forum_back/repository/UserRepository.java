package com.construction_worker_forum_back.repository;

import com.construction_worker_forum_back.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameIgnoreCase(String username);

    int deleteByUsernameIgnoreCase(String username);

    Optional<User> findByUsernameIgnoreCase(String username);
}

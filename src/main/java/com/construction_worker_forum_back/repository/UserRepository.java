package com.construction_worker_forum_back.repository;

import com.construction_worker_forum_back.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameIgnoreCase(String username);

    int deleteByUsernameIgnoreCase(String username);
}

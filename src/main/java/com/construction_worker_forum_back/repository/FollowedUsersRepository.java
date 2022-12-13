package com.construction_worker_forum_back.repository;

import com.construction_worker_forum_back.model.entity.FollowedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowedUsersRepository extends JpaRepository<FollowedUser, Long> {

    @Query("select f from FollowedUser f inner join fetch f.followedUsers where f.followingUser.id = ?1")
    List<FollowedUser> findAllFollowedUsersByUserId(Long id);

    @Query("select f from FollowedUser f inner join fetch f.followingUser where f.followedUsers.id = ?1")
    List<FollowedUser> findAllFollowersByUserId(Long id);

    @Query("select f from FollowedUser f where f.followedUsers.id = ?1 and  f.followingUser.id = ?2")
    Optional<FollowedUser> findByFollowedUsersIdAndFollowerId(Long followedUserId, Long followerId);

    int deleteByFollowedUsers_IdAndFollowingUser_Id(Long followedUserId, Long followerId);

}

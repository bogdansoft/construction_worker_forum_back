package com.construction_worker_forum_back.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Table(name = "followed_users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowedUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "followed_user_id")
    private Long followedUserId;

    @Column(name = "following_user_id")
    private Long followingUserId;
}

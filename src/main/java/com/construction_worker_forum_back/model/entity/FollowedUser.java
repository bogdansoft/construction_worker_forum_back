package com.construction_worker_forum_back.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "followed_users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "followed_user_id", referencedColumnName = "id")
    private User followedUsers;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "following_user_id", referencedColumnName = "id")
    private User followingUser;
}

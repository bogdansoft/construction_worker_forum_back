package com.construction_worker_forum_back.model.entity;

import com.construction_worker_forum_back.model.security.AccountStatus;
import com.construction_worker_forum_back.model.security.Role;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@Cache(region = "userCache", usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -6470090944414208496L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Size(min = 4, max = 20)
    private String username;

    @NotNull
    private String password;

    @Column(unique = true)
    @Email
    private String email;

    @Size(max = 30)
    private String firstName;

    @Size(max = 30)
    private String lastName;

    @Size(min = 5)
    private String bio;

    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Role userRoles = Role.USER;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> userComments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> userPosts;

    @OneToMany(mappedBy = "followingUser", cascade = CascadeType.ALL)
    private List<FollowedUser> followedUsers;

    @OneToMany(mappedBy = "followedUsers", cascade = CascadeType.ALL)
    private List<FollowedUser> followedUser;

    @ManyToMany(targetEntity = Post.class, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "post_follow",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id")
    )
    private Set<Post> followedPosts = new HashSet<>();

    @ManyToMany(targetEntity = Post.class, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "post_like",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id")
    )
    private Set<Post> likedPosts = new HashSet<>();

    @ManyToMany(targetEntity = Comment.class, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "comment_like",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id")
    )
    private Set<Comment> likedComments = new HashSet<>();

    @PrePersist
    private void beforeSaving() {
        createdAt = Date.from(Instant.now());
        accountStatus = AccountStatus.ACTIVE;
    }
}

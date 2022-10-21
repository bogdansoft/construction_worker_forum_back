package com.construction_worker_forum_back.model.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Fetch;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 1, max = 100)
    @Column(columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinTable(
            name = "users_user_comments",
            joinColumns = @JoinColumn(name = "user_comments_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinTable(
            name = "posts_comments",
            joinColumns = @JoinColumn(name = "comments_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Post post;

    @PrePersist
    private void beforeSaving() {
        createdAt = Date.from(Instant.now());
    }
}

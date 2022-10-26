package com.construction_worker_forum_back.model.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 1)
    private String title;

    @Size(min = 1, max = 1000)
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

    @PrePersist
    private void beforeSaving() {
        createdAt = Date.from(Instant.now());
    }
}

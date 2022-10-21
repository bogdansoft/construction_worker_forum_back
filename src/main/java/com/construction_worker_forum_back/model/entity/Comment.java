package com.construction_worker_forum_back.model.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    @PrePersist
    private void beforeSaving() {
        createdAt = Date.from(Instant.now());
    }
}

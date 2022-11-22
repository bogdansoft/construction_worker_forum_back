package com.construction_worker_forum_back.model.entity;

import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "topics")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Cache(region = "topicCache", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Topic implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 20)
    private String name;

    @Size(min = 1, max = 1000)
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, targetEntity = User.class, optional = false)
    @JoinColumn(name = "last_edited_by", referencedColumnName = "id")
    private User lastEditor;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, targetEntity = User.class, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Post> posts;

    @PrePersist
    private void beforeSaving() {
        createdAt = Date.from(Instant.now());
    }
}

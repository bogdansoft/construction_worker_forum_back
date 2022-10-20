package com.construction_worker_forum_back.model.entity;

import com.construction_worker_forum_back.model.security.AccountStatus;
import com.construction_worker_forum_back.model.security.Role;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

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

    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Role userRoles = Role.USER;

    @ToString.Exclude
    @OneToMany
    private List<Comment> userComments;

    @PrePersist
    private void beforeSaving() {
        createdAt = Date.from(Instant.now());
        accountStatus = AccountStatus.ACTIVE;
    }
}

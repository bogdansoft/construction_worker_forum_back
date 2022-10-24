package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.security.AccountStatus;
import com.construction_worker_forum_back.model.security.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private Date createdAt;
    private Date updatedAt;
    private AccountStatus accountStatus;
    private Role userRoles;
    private List<CommentDto> userComments;
    private List<PostDto> userPosts;
}

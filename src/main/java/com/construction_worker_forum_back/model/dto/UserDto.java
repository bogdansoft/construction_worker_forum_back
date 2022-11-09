package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.dto.simple.CommentSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.PostSimpleDto;
import com.construction_worker_forum_back.model.security.AccountStatus;
import com.construction_worker_forum_back.model.security.Role;
import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;
    private AccountStatus accountStatus;
    private Role userRoles;
    private List<CommentSimpleDto> userComments;
    private List<PostSimpleDto> userPosts;
    private List<CommentSimpleDto> likedComments;
    private List<PostSimpleDto> likedPosts;
}

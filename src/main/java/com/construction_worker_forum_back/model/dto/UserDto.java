package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.dto.simple.CommentSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.PostSimpleDto;
import com.construction_worker_forum_back.model.security.AccountStatus;
import com.construction_worker_forum_back.model.security.Role;
import lombok.*;
import org.springframework.cache.annotation.Cacheable;
import java.io.*;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6470090944414208496L;
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String avatar;
    private byte[] avatarBytes;
    private Date createdAt;
    private Date updatedAt;
    private AccountStatus accountStatus;
    private Role userRoles;
    private List<CommentSimpleDto> userComments;
    private List<PostSimpleDto> userPosts;
    private List<CommentSimpleDto> likedComments;
    private List<PostSimpleDto> likedPosts;
}

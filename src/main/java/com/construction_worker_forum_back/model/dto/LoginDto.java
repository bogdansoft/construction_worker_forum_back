package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.security.UserDetailsImpl;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDto {
    private String token;
    private Long id;
    private String username;

    public LoginDto(String token, UserDetailsImpl userDetails) {
        this.token = token;
        this.id = userDetails.getUser().getId();
        this.username = userDetails.getUsername();
    }
}

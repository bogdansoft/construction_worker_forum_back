package com.construction_worker_forum_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    @NotEmpty
    @Size(min = 4, max = 20)
    private String username;

    @NotEmpty
    private String password;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(max = 30)
    private String firstName;

    @NotEmpty
    @Size(max = 30)
    private String lastName;

    @Size(min = 5)
    private String bio;
}

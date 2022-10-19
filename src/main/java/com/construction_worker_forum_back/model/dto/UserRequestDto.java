package com.construction_worker_forum_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    @Size(min = 4, max = 20)
    private String username;

    @NotNull
    private String password;

    @Email
    private String email;

    @Size(max = 30)
    private String firstName;

    @Size(max = 30)
    private String lastName;
}

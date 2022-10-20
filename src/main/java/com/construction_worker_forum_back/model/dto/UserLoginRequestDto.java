package com.construction_worker_forum_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDto {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}

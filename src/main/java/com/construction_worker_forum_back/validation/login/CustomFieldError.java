package com.construction_worker_forum_back.validation.login;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldError {
    private String field;
    private String message;
}

package com.construction_worker_forum_back.validation.login;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorResponse {
    private List<CustomFieldError> fieldErrors;
}

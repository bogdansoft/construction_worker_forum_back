package com.construction_worker_forum_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {

    //  @NotEmpty
    //    private Long userId;

    @NotEmpty
    @Size(min = 1, max = 150)
    private String content;

    @NotEmpty
    @Size(min = 2, max = 20)
    private String title;
}

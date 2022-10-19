package com.construction_worker_forum_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

    @Size(min = 1, max = 100)
    private String content;

    @NotEmpty
    private Long postId;

    @NotEmpty
    private Long userId;
}

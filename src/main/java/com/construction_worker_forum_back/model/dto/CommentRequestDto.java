package com.construction_worker_forum_back.model.dto;

import liquibase.pro.packaged.B;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CommentRequestDto {

    @Size(min = 1, max = 100)
    private String content;

    @NotNull
    private Long postId;

    @NotNull
    private Long userId;
}

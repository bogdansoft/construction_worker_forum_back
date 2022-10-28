package com.construction_worker_forum_back.model.dto;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequestDto {

    @Size(min = 3, max = 50)
    private String name;

    private Long postId;
}

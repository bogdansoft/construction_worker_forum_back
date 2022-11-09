package com.construction_worker_forum_back.model.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequestDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String name;

    @NotNull
    @Size(min = 1, max = 1000)
    private String description;

    @NotNull
    private Long userId;
}

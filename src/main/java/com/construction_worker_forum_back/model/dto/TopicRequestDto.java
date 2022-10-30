package com.construction_worker_forum_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequestDto {

    @Size(min = 3, max = 50)
    private String name;

    @Size(min = 1, max = 1000)
    private String description;

    @NotNull
    private Long userId;
}

package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.entity.Keyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    @Size(min = 1, max = 1000)
    private String content;

    @NotNull
    @Size(min = 2, max = 50)
    private String title;

    @NotNull
    private Long topicId;

    private Set<Keyword> keywords;
}

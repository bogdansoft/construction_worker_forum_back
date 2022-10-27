package com.construction_worker_forum_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicDto {
    private Long id;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private List<PostDto> posts;
}

package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.dto.simple.PostSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TopicDto {
    private Long id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private UserSimpleDto user;
    private List<PostSimpleDto> posts;
}

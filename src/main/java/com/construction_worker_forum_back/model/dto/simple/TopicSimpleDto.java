package com.construction_worker_forum_back.model.dto.simple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicSimpleDto {
    private Long id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
}

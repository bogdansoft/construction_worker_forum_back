package com.construction_worker_forum_back.model.dto.simple;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TopicSimpleDto {
    private Long id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
}

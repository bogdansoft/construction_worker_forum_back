package com.construction_worker_forum_back.model.dto.simple;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostSimpleDto {
    private Long id;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}

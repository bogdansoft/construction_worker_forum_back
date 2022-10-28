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
public class CommentSimpleDto {
    private Long id;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}

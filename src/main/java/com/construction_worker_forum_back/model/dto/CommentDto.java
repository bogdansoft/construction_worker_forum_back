package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.dto.simple.PostSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private UserSimpleDto user;
    private PostSimpleDto post;
}

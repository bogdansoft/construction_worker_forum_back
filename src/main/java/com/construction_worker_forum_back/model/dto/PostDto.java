package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.dto.simple.CommentSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.LikerSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.TopicSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import com.construction_worker_forum_back.model.entity.Keyword;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private UserSimpleDto user;
    private List<CommentSimpleDto> comments;
    private List<LikerSimpleDto> likers;
    private TopicSimpleDto topic;
    private Set<Keyword> keywords;
}

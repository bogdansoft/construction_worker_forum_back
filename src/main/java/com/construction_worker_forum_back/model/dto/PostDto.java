package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.dto.simple.CommentSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.LikerSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.TopicSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import lombok.*;
import org.springframework.cache.annotation.Cacheable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
public class PostDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -6470090944414208496L;
    private Long id;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private UserSimpleDto user;
    private List<CommentSimpleDto> comments;
    private List<LikerSimpleDto> likers;
    private TopicSimpleDto topic;
}

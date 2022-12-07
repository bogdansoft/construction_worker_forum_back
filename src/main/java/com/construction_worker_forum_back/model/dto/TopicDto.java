package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.dto.simple.PostSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import lombok.*;

import org.springframework.cache.annotation.Cacheable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
public class TopicDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -6470090944414208496L;
    private Long id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private UserSimpleDto user;
    private List<PostSimpleDto> posts;
}

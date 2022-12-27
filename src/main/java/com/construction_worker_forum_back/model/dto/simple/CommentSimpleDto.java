package com.construction_worker_forum_back.model.dto.simple;

import lombok.*;
import org.springframework.cache.annotation.Cacheable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
public class CommentSimpleDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -6470090944414208496L;
    private Long id;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}

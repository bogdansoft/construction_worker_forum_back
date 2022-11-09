package com.construction_worker_forum_back.model.dto.simple;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikerSimpleDto {
    private Long id;
    private String username;
}

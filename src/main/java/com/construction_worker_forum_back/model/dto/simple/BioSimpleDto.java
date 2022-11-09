package com.construction_worker_forum_back.model.dto.simple;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class BioSimpleDto {
    private String newBio;
}

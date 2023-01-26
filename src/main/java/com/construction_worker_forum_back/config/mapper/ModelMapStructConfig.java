package com.construction_worker_forum_back.config.mapper;

import com.construction_worker_forum_back.model.dto.simple.FollowerSimpleDto;
import com.construction_worker_forum_back.model.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ModelMapStructConfig {
    FollowerSimpleDto toFollowerSimpleDto(User user);

    User toUser(FollowerSimpleDto dto);

    List<FollowerSimpleDto> toListFollowerSimpleDto(List<User> users);

    List<User> toListUsers(List<FollowerSimpleDto> dtos);
}
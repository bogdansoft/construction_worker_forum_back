package com.construction_worker_forum_back.config;

import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.entity.IEntity;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper setup() {
        var mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<List<? extends IEntity>, List<Long>> entityListToLongListConverter =
                context -> context.getSource()
                        .stream()
                        .map(IEntity::getId)
                        .collect(Collectors.toList());

        mapper.createTypeMap(User.class, UserDto.class)
                .addMappings(map -> map
                        .using(entityListToLongListConverter)
                        .map(
                                User::getUserPosts,
                                UserDto::setUserPostsIds
                        ))
                .addMappings(map -> map
                        .using(entityListToLongListConverter)
                        .map(
                                User::getUserComments,
                                UserDto::setUserCommentsIds
                        ));

        mapper.createTypeMap(Post.class, PostDto.class)
                .addMappings(map -> map
                        .using(entityListToLongListConverter)
                        .map(
                                Post::getComments,
                                PostDto::setCommentsIds
                        ))
                .addMapping(map -> map.getUser().getId(), PostDto::setUserId);

        return mapper;
    }
}

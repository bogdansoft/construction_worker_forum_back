package com.construction_worker_forum_back.config.mapper;

import com.construction_worker_forum_back.model.dto.CommentDto;
import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.simple.FollowerSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.LikerSimpleDto;
import com.construction_worker_forum_back.model.entity.Comment;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper setup() {
        var mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<Set<User>, List<LikerSimpleDto>> usersToLikersListConverter =
                context -> context.getSource()
                        .stream()
                        .map(user -> mapper.map(user, LikerSimpleDto.class))
                        .toList();

        Converter<Set<User>, List<FollowerSimpleDto>> usersToFollowersListConverter =
                context -> context.getSource()
                        .stream()
                        .map(user -> mapper.map(user, FollowerSimpleDto.class))
                        .toList();

        mapper.createTypeMap(Post.class, PostDto.class)
                .addMappings(map -> map
                        .using(usersToLikersListConverter)
                        .map(Post::getLikers, PostDto::setLikers))
                .addMappings(map -> map
                        .using(usersToFollowersListConverter)
                        .map(Post::getFollowers, PostDto::setFollowers));

        mapper.createTypeMap(Comment.class, CommentDto.class)
                .addMappings(map -> map
                        .using(usersToLikersListConverter)
                        .map(Comment::getLikers, CommentDto::setLikers));

        return mapper;
    }
}

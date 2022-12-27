package com.construction_worker_forum_back.cache.embedded;

import com.construction_worker_forum_back.client.NotificationClient;
import com.construction_worker_forum_back.config.redis.RedisConfig;
import com.construction_worker_forum_back.model.dto.CommentDto;
import com.construction_worker_forum_back.model.entity.Comment;
import com.construction_worker_forum_back.repository.CommentRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.CommentService;
import com.construction_worker_forum_back.service.PostService;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@Import({RedisConfig.class, CommentService.class})
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class,
        EmbeddedRedisConfiguration.class
})
@EnableCaching
@TestExecutionListeners(listeners = { EmbeddedRedisConfiguration.class }, mergeMode = MERGE_WITH_DEFAULTS)
public class CommentServiceCacheTest {
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private PostService postService;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private NotificationClient notificationClient;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CacheManager cacheManager;

    @Test
    void givenRedisCaching_whenFindCommentById_thenCommentReturnedFromCache() {
        //Given
        Comment comment = new Comment();
        comment.setId(100L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(modelMapper.map(comment, CommentDto.class)).willReturn(CommentDto.builder().id(100L).build());

        //When
        commentService.findById(comment.getId());
        commentService.findById(comment.getId());

        //Then
        verify(commentRepository, times(1)).findById(anyLong());
    }
}

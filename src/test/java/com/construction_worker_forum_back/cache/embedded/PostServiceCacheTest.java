package com.construction_worker_forum_back.cache.embedded;

import com.construction_worker_forum_back.client.NotificationClient;
import com.construction_worker_forum_back.config.redis.RedisConfig;
import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.repository.PostRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.PostService;
import com.construction_worker_forum_back.service.TopicService;
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

@Import({RedisConfig.class, PostService.class})
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class,
        EmbeddedRedisConfiguration.class
})
@EnableCaching
@TestExecutionListeners(listeners = { EmbeddedRedisConfiguration.class }, mergeMode = MERGE_WITH_DEFAULTS)
class PostServiceCacheTest {

    @MockBean
    private PostRepository postRepository;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TopicService topicService;
    @MockBean
    private UserService userService;
    @MockBean
    private NotificationClient notificationClient;
    @Autowired
    private PostService postService;
    @Autowired
    private CacheManager cacheManager;

    @Test
    void givenRedisCaching_whenFindPostById_thenPostReturnedFromCache() {
        Post post = new Post();
        post.setId(100L);
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());

        given(postRepository.findById(100L)).willReturn(Optional.of(post));
        given(modelMapper.map(post, PostDto.class)).willReturn(postDto);

        //when
        postService.findById(post.getId()).orElseThrow();
        postService.findById(post.getId()).orElseThrow();

        verify(postRepository, times(1)).findById(anyLong());
    }
}

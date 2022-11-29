package com.construction_worker_forum_back.cache;

import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.repository.PostRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.PostService;
import com.construction_worker_forum_back.service.TopicService;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
//@TestComponent
@Import(TestRedisConfiguration.class)
@ExtendWith(SpringExtension.class)
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
public class PostCacheServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TopicService topicService;
    @Mock
    private UserService userService;
    @Mock
    private CacheManager cacheManager;
    @InjectMocks
    private PostService postService;

    @Test
    void itShouldGetByIdPost() {
        //given
        Post post = new Post();
        post.setId(1L);
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());

        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);
        var expectedFromDb = postService.findById(post.getId()).orElseThrow();
        var expectedFromCache = postService.findById(post.getId()).orElseThrow();

        //then
        assertEquals(expectedFromDb, postDto);
        assertEquals(expectedFromCache, postDto);

        verify(postRepository, times(1)).findById(anyLong());
    }
}

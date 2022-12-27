package com.construction_worker_forum_back.cache.test_contatiners;

import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.repository.PostRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.PostService;
import com.construction_worker_forum_back.service.TopicService;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(PostService.class)
public class PostServiceCacheContainerTest extends AbstractTestContainerSetUpClass {
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

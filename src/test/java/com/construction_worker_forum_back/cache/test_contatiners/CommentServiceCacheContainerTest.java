package com.construction_worker_forum_back.cache.test_contatiners;

import com.construction_worker_forum_back.client.NotificationClient;
import com.construction_worker_forum_back.model.dto.CommentDto;
import com.construction_worker_forum_back.model.entity.Comment;
import com.construction_worker_forum_back.repository.CommentRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.CommentService;
import com.construction_worker_forum_back.service.PostService;
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

@Import(CommentService.class)
public class CommentServiceCacheContainerTest extends AbstractTestContainerSetUpClass {
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


package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.client.NotificationClient;
import com.construction_worker_forum_back.model.dto.CommentDto;
import com.construction_worker_forum_back.model.dto.CommentRequestDto;
import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.simple.CommentSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.LikerSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.PostSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import com.construction_worker_forum_back.model.entity.Comment;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.CommentRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentService commentService;


    @Test
    void itShouldGetAllComments() {
        //Given
        List<Comment> commentList = new ArrayList<>(List.of(new Comment()));
        given(commentRepository.findAll()).willReturn(commentList);

        //When
        var expected = commentService.getAllComments();

        //Then
        assertEquals(expected.size(), commentList.size());
        verify(commentRepository, atLeastOnce()).findAll();
    }

    @Test
    void itShouldGetByUsername() {
        //Given
        User user = new User();
        user.setUsername("adam");
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setId(1L);
        List<Comment> commentList = new ArrayList<>(List.of(comment));

        given(commentRepository.findByUser_UsernameIgnoreCase(user.getUsername())).willReturn(commentList);
        given(modelMapper.map(comment, CommentDto.class)).willReturn(CommentDto.builder().id(1L).build());

        //When
        var expected = commentService.getCommentsByUsername(user.getUsername());

        //Then
        assertEquals(expected.size(), commentList.size());
        verify(commentRepository, atLeastOnce()).findByUser_UsernameIgnoreCase(user.getUsername());
    }


    @Test
    void itShouldGetCommentLikers() {
        //Given
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).likers(new HashSet<>(List.of(user))).build();

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(modelMapper.map(user, LikerSimpleDto.class)).willReturn(LikerSimpleDto.builder().id(1L).build());

        //When
        var expected = commentService.getCommentLikers(comment.getId());

        //Then
        assertEquals(expected.size(), comment.getLikers().size());
        verify(commentRepository, atLeastOnce()).findById(comment.getId());
    }

    @Test
    void itShouldGetByIdUser() {
        //Given
        Comment comment = new Comment();
        comment.setId(1L);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(modelMapper.map(comment, CommentDto.class)).willReturn(CommentDto.builder().id(1L).build());

        //When
        var expected = commentService.findById(comment.getId());

        //Then
        assertTrue(expected.isPresent());
        verify(commentRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void itShouldLikeComment() {
        //Given
        User user = User.builder().username("adam").id(1L).build();
        Comment comment = Comment.builder().id(1L).likers(new HashSet<>(List.of(User.builder().id(2L).build()))).build();
        CommentDto commentDto = CommentDto.builder().id(1L).likers(new ArrayList<>(List.of(LikerSimpleDto.builder().id(2L).build()))).build();
        user.setLikedComments(new HashSet<>(List.of(comment)));

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(modelMapper.map(comment, CommentDto.class)).willReturn(commentDto);

        //When
        var expected = commentService.likeComment(comment.getId(), user.getId());

        //Then
        assertNotNull(expected.getLikers());

        verify(userRepository, atLeastOnce()).findById(user.getId());
        verify(commentRepository, atLeastOnce()).findById(comment.getId());
        verify(modelMapper, atLeastOnce()).map(comment, CommentDto.class);
    }

    @Test
    void itShouldDislikeComment() {
        //Given
        User user = User.builder().username("adam").id(1L).build();
        Comment comment = Comment.builder().id(1L).likers(new HashSet<>(List.of(user))).build();
        user.setLikedComments(new HashSet<>(List.of(comment)));

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));


        //When
        commentService.unlikeComment(comment.getId(), user.getId());

        //Then
        verify(userRepository, atLeastOnce()).findById(user.getId());
        verify(commentRepository, atLeastOnce()).findById(comment.getId());
    }

    @Test
    void itShouldUpdateCommentById() {
        //Given
        Long commentToUpdateId = 1L;

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .content("good")
                .userId(1L)
                .build();

        User user = User.builder()
                .id(1L)
                .username("adam")
                .build();

        Comment comment = Comment.builder()
                .id(commentToUpdateId)
                .content(commentRequestDto.getContent())
                .user(user)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(commentToUpdateId)
                .content(comment.getContent())
                .user(UserSimpleDto.builder()
                        .id(1L)
                        .username("adam")
                        .build())
                .build();

        given(commentRepository.findById(commentToUpdateId)).willReturn(Optional.of(comment));
        doNothing().when(modelMapper).map(commentRequestDto, comment);
        given(modelMapper.map(comment, CommentDto.class)).willReturn(commentDto);

        //When
        var expected = commentService.updateCommentById(commentToUpdateId, commentRequestDto);

        //Then
        assertNotNull(expected);
        assertEquals(expected.getContent(), commentRequestDto.getContent());
        assertEquals(expected.getUser().getId(), user.getId());
        assertEquals(expected.getUser().getUsername(), user.getUsername());

        verify(commentRepository, atLeastOnce()).findById(commentToUpdateId);
        verify(modelMapper, atLeastOnce()).map(comment, CommentDto.class);
    }

}

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
    void itShouldCreateComment() {
        //Given
        CommentRequestDto commentRequestDto = CommentRequestDto.builder().content("good").userId(1L).postId(1L).build();

        CommentDto commentDto = CommentDto.builder().content("good")
                .user(UserSimpleDto.builder().id(1L).username("adam").build())
                .post(PostSimpleDto.builder().id(1L).content("post").build())
                .build();

        User user = User.builder().id(1L).username("adam").build();

        UserDto userDto = UserDto.builder().id(1L).username("adam").build();

        Post post = Post.builder().id(1L).content("post").build();

        PostDto postDto = PostDto.builder().id(1L).content("post").build();

        Comment comment = Comment.builder().id(1L).build();


        given(modelMapper.map(commentRequestDto, Comment.class)).willReturn(comment);
        given(modelMapper.map(userDto, User.class)).willReturn(user);
        given(modelMapper.map(postDto, Post.class)).willReturn(post);
        given(modelMapper.map(comment, CommentDto.class)).willReturn(commentDto);
        given(userService.findById(commentRequestDto.getUserId())).willReturn(Optional.of(userDto));
        given(postService.findById(commentRequestDto.getPostId())).willReturn(Optional.of(postDto));
        given(commentRepository.save(comment)).willReturn(comment);

        //When
        var expected = commentService.createComment(commentRequestDto, null);

        //Then
        assertNotNull(expected);
        assertEquals(expected.getContent(), commentDto.getContent());
        assertEquals(expected.getUser().getId(), userDto.getId());
        assertEquals(expected.getPost().getId(), postDto.getId());

        verify(commentRepository, atLeastOnce()).save(comment);
    }

    @Test
    void itShouldCreateSubComment() {
        //Given
        CommentRequestDto primaryCommentRequestDto = CommentRequestDto.builder()
                .content("good")
                .userId(1L)
                .postId(1L)
                .build();

        CommentRequestDto subCommentRequestDto = CommentRequestDto.builder()
                .content("sub-comment test")
                .userId(1L)
                .postId(1L)
                .build();

        User userFromRequestDto = User.builder()
                .id(1L)
                .username("adam")
                .build();

        UserDto userDto = UserDto.builder()
                .id(userFromRequestDto.getId())
                .username(userFromRequestDto.getUsername())
                .build();

        UserSimpleDto userSimpleDto = UserSimpleDto.builder()
                .id(userDto.getId()).username(userDto.getUsername())
                .build();

        Post postFromRequestDto = Post.builder()
                .id(1L)
                .content("post")
                .build();

        PostDto postDto = PostDto.builder()
                .id(postFromRequestDto.getId())
                .content(postFromRequestDto.getContent())
                .build();

        PostSimpleDto postSimpleDto = PostSimpleDto.builder()
                .id(postDto.getId()).content(postDto.getContent())
                .build();

        Comment primaryComment = Comment.builder()
                .id(1L)
                .content(primaryCommentRequestDto.getContent())
                .user(userFromRequestDto)
                .post(postFromRequestDto)
                .parentComment(null)
                .subComments(new HashSet<>())
                .createdAt(Date.from(Instant.now()))
                .build();

        Comment subComment = Comment.builder()
               .id(2L)
                .content(subCommentRequestDto.getContent())
                .user(userFromRequestDto)
                .post(postFromRequestDto)
                .parentComment(primaryComment)
                .subComments(new HashSet<>())
                .createdAt(Date.from(Instant.now()))
                .build();

        CommentSimpleDto parentCommentSimpleDto = CommentSimpleDto.builder()
                .id(primaryComment.getId())
                .build();

        CommentDto subCommentDto = CommentDto.builder()
                .id(subComment.getId())
                .content(subComment.getContent())
                .user(userSimpleDto)
                .post(postSimpleDto)
                .parentComment(parentCommentSimpleDto)
                .build();

        Long commentForReplyId = primaryComment.getId();

        //sub-comment
        given(commentRepository.findById(commentForReplyId)).willReturn(Optional.of(primaryComment));
        given(commentRepository.save(primaryComment)).willReturn(primaryComment);
        given(commentRepository.save(subComment)).willReturn(subComment);
        given(modelMapper.map(subCommentRequestDto, Comment.class)).willReturn(subComment);
        given(modelMapper.map(subComment, CommentDto.class)).willReturn(subCommentDto);

        //user & post
        given(userService.findById(primaryCommentRequestDto.getUserId())).willReturn(Optional.of(userDto));
        given(postService.findById(primaryCommentRequestDto.getPostId())).willReturn(Optional.of(postDto));
        given(modelMapper.map(userDto, User.class)).willReturn(userFromRequestDto);
        given(modelMapper.map(postDto, Post.class)).willReturn(postFromRequestDto);

        //When
        var expectedSubComment = commentService.createComment(subCommentRequestDto, commentForReplyId);

        //Then
        assertNotNull(expectedSubComment);
        assertEquals(expectedSubComment.getContent(), subCommentDto.getContent());
        assertEquals(expectedSubComment.getUser().getId(), userDto.getId());
        assertEquals(expectedSubComment.getPost().getId(), postDto.getId());
        assertEquals(expectedSubComment.getParentComment().getId(), primaryComment.getId());

        verify(commentRepository, atLeastOnce()).save(primaryComment);
        verify(commentRepository, atLeastOnce()).save(subComment);
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
    void itShouldDeleteComment() {
        //Given
        User user = User.builder().username("adam").id(1L).build();
        Comment comment = Comment.builder().id(1L).user(user).build();

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(commentRepository.deleteCommentById(comment.getId())).willReturn(1);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        //When
        var expected = commentService.deleteById(comment.getId(), user.getId());

        //Then
        assertTrue(expected);
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

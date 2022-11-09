package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.dto.TopicDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.Topic;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.PostRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

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
    @InjectMocks
    private PostService postService;

    @Test
    void itShouldCreatePost() {
        //given
        PostRequestDto postRequestDto = new PostRequestDto();
        User user = new User();
        user.setId(1L);
        postRequestDto.setUserId(user.getId());
        Topic topic = new Topic();
        topic.setId(1L);
        postRequestDto.setTopicId(topic.getId());
        Post post = new Post();
        post.setUser(user);
        post.setId(1L);
        UserDto userDto = new UserDto();
        //Post post = modelMapper.map(postRequestDto, Post.class);
        //UserDto userDto = modelMapper.map(user, UserDto.class);
        //TopicDto topicDto = modelMapper.map(topic, TopicDto.class);
        /*
        given(userRepository.findById(user.getId())).willReturn(Optional.of(userDto));
        given(topicRepository.findTopicById(topic.getId())).willReturn(Optional.of(topicDto));*/
        given(modelMapper.map(user, UserDto.class)).willReturn(new UserDto());
        given(modelMapper.map(topic, TopicDto.class)).willReturn(new TopicDto());
        given(modelMapper.map(postRequestDto, Post.class)).willReturn(new Post());
        //given(modelMapper.map(userDto, User.class)).willReturn(new User());
        given(userService.findById(user.getId())).willReturn(Optional.of(new UserDto()));
        given(topicService.findTopicById(topic.getId())).willReturn(Optional.of(new TopicDto()));
        given(postRepository.save(post)).willReturn(post);

        //when
        postService.createPost(postRequestDto);

        //then
        verify(postRepository, atLeastOnce()).save(any());
    }

    @Test
    void itShouldGetAllPosts() {
        //given
        Post post = new Post();
        List<Post> postList = new ArrayList<>(List.of(post));
        given(modelMapper.map(post, PostDto.class)).willReturn(new PostDto());
        given(postRepository.findAll()).willReturn(postList);

        //when
        var expected = postService.getAllPosts();

        //then
        assertTrue(expected.size() > 0);

        verify(postRepository, atLeastOnce()).findAll();
    }

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
        var expected = postService.findById(post.getId());

        //then
        assertTrue(expected.isPresent());

        verify(postRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void itShouldGetAllPostLikers() {
        //given
        Set<User> likers = new HashSet<>(Set.of(new User()));
        Post post = new Post();
        post.setId(1L);
        post.setLikers(likers);
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        //when
        var expected = postService.getPostLikers(post.getId());

        //then
        assertTrue(expected.size() > 0);

        verify(postRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void itShouldGetPostByTopicId() {
        //given
        Post post = new Post();
        post.setId(1L);
        Topic topic = new Topic();
        topic.setId(1L);
        post.setTopic(topic);
        List<Post> postList = new ArrayList<>(List.of(post));
        given(postRepository.findByTopic_Id(post.getTopic().getId())).willReturn(postList);

        //when
        var expected = postService.getPostsByTopicId(post.getTopic().getId());

        //then
        assertTrue(expected.size() > 0);

        verify(postRepository, atLeastOnce()).findByTopic_Id(anyLong());
    }

    @Test
    void itShouldFindPostByContentOrTitle() {
        //given
        Post post = new Post();
        post.setTitle("test");
        post.setContent("test");
        List<Post> postList = new ArrayList<>(List.of(post));
        given(postRepository.findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(
                post.getTitle(),
                post.getContent()))
                .willReturn(postList);

        //when
        var expected = postService.findPostByContentOrTitle(post.getTitle());

        //then
        assertTrue(expected.size() > 0);

        verify(postRepository, atLeastOnce()).findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(anyString(), anyString());
    }

    @Test
    void itShouldUnlikePost() {
        //given
        Post post = new Post();
        post.setId(1L);
        User user = new User();
        user.setId(1L);
        post.setUser(user);
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        //when
        postService.unlikePost(post.getId(), user.getId());

        //then
        verify(postRepository, atLeastOnce()).findById(anyLong());
        verify(userRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void itShouldLikePost() {
        //given
        Post post = new Post();
        post.setId(1L);
        User user = new User();
        user.setId(1L);
        post.setUser(user);
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        //when
        postService.likePost(post.getId(), user.getId());

        //then
        verify(postRepository, atLeastOnce()).findById(anyLong());
        verify(userRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void itShouldGetByUsernamePost() {
        //given
        Post post = new Post();
        post.setId(1L);
        User user = new User();
        user.setUsername("test_user");
        var posts = List.of(post);
        user.setUserPosts(posts);

        //when
        when(postRepository.findByUser_UsernameIgnoreCase(user.getUsername())).thenReturn(posts);
        postService.getPostsByUsername(user.getUsername());

        //then
        verify(postRepository, atLeastOnce()).findByUser_UsernameIgnoreCase(anyString());
    }

    @Test
    void itShouldDeletePost() {
        //given
        Post post = new Post();
        post.setId(1L);

        //when
        postService.deleteById(post.getId());

        //then
        verify(postRepository, atLeastOnce()).deletePostById(anyLong());
    }
}

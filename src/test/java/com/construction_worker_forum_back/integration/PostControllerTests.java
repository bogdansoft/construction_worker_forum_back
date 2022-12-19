package com.construction_worker_forum_back.integration;

import com.construction_worker_forum_back.config.security.JwtTokenUtil;
import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.Topic;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.model.security.AccountStatus;
import com.construction_worker_forum_back.model.security.Role;
import com.construction_worker_forum_back.model.security.UserDetailsImpl;
import com.construction_worker_forum_back.repository.CommentRepository;
import com.construction_worker_forum_back.repository.PostRepository;
import com.construction_worker_forum_back.repository.TopicRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("dev")
class PostControllerTests extends TestcontainersConfig {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    RemoveService removeService;
    @Autowired
    JwtTokenUtil tokenUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    private UserDetailsImpl userDetails;

    private User savedUser;

    private Topic savedTopic;

    private void setupUser() {
        User user = User.builder()
                .username("user")
                .password("password2")
                .email("userunique@example.com")
                .userRoles(Role.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        savedUser = userRepository.save(user);

        userDetails = new UserDetailsImpl(user);
    }

    private void setupTopic() {
        Topic topic = Topic.builder()
                .user(savedUser)
                .name("Tower cranes")
                .description("Test description")
                .build();

        savedTopic = topicRepository.save(topic);
    }

    @BeforeEach
    void setUp() {
        removeService.removeAll();
        setupUser();
        setupTopic();
    }

    @Test
    void givenAuthenticatedUser_whenCreatingPost_returnPostAndStatusCreated() throws Exception {

        //given
        PostRequestDto post = PostRequestDto.builder()
                .userId(savedUser.getId())
                .topicId(savedTopic.getId())
                .content("New post")
                .title("Title of new post")
                .build();

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails))
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.user").exists());
    }

    @Test
    void givenUnauthorizedUser_whenAddingPost_returnsStatusUnauthorized() throws Exception {
        //given
        PostRequestDto post = PostRequestDto.builder()
                .userId(savedUser.getId())
                .topicId(savedTopic.getId())
                .content("New post")
                .title("Title of new post")
                .build();

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenPostExistsInDb_WhenRequested_ThenItReturnsPostDto() throws Exception {

        //given
        PostRequestDto post = PostRequestDto.builder()
                .userId(savedUser.getId())
                .topicId(savedTopic.getId())
                .content("New post")
                .title("Title of new post")
                .build();
        Post postFromDb = postRepository.save(modelMapper.map(post, Post.class));

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/")
                        .param("id", String.valueOf(postFromDb.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(postFromDb.getId()))
                .andExpect(jsonPath("$[0].title").value(postFromDb.getTitle()))
                .andExpect(jsonPath("$[0].content").value(postFromDb.getContent()));

    }

    @Test
    void givenAuthorizedUser_whenEditingPost_ThenReturnsUpdatedPostDto() throws Exception {
        //given
        PostRequestDto post = PostRequestDto.builder()
                .userId(savedUser.getId())
                .topicId(savedTopic.getId())
                .content("New post")
                .title("Title of new post")
                .build();

        Post mappedPost = modelMapper.map(post, Post.class);
        mappedPost.setUser(savedUser);
        Post savedPost = postRepository.save(mappedPost);


        PostRequestDto editedPost = PostRequestDto.builder()
                .userId(post.getUserId())
                .topicId(post.getTopicId())
                .content("changed Content")
                .title("Title of changed post")
                .build();

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/{id}", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editedPost))
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails)))
                .andExpect(jsonPath("$.title").value(editedPost.getTitle()))
                .andExpect(jsonPath("$.content").value(editedPost.getContent()));
    }

    @Test
    void givenAuthorizedUser_WhenDeletingPost_ThenSuccess() throws Exception {
        //given
        PostRequestDto post = PostRequestDto.builder()
                .userId(savedUser.getId())
                .topicId(savedTopic.getId())
                .content("New post")
                .title("Title of new post")
                .build();
        Post postFromDb = postRepository.save(modelMapper.map(post, Post.class));

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post/{id}", postFromDb.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails)))
                .andExpect(jsonPath("$.status").value("Deleted successfully!"));
    }

    @Test
    void givenAuthorizedUser_WhenLikingPost_ThenReturnCreatedAndPostDto() throws Exception {
        //given
        PostRequestDto post = PostRequestDto.builder()
                .userId(savedUser.getId())
                .topicId(savedTopic.getId())
                .content("New post")
                .title("Title of new post")
                .build();
        Post postFromDb = postRepository.save(modelMapper.map(post, Post.class));

        User user = User.builder()
                .username("toot")
                .password("toot1234")
                .email("toot@test.com")
                .firstName("Doe")
                .lastName("John")
                .build();
        User userToLike = userRepository.save(user);
        UserDetailsImpl userDetailsLiker = new UserDetailsImpl(userToLike);

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("postId", String.valueOf(postFromDb.getId()))
                        .param("userId", String.valueOf(userToLike.getId()))
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsLiker)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.likers.length()").value(1))
                .andExpect(jsonPath("$.likers[0].username").value(userToLike.getUsername()));
    }

    @Test
    void givenAuthorizedUser_WhenFollowingPost_ThenReturnCreatedAndPostDto() throws Exception {
        //given
        PostRequestDto post = PostRequestDto.builder()
                .userId(savedUser.getId())
                .topicId(savedTopic.getId())
                .content("New post")
                .title("Title of new post")
                .build();
        Post postFromDb = postRepository.save(modelMapper.map(post, Post.class));

        User user = User.builder()
                .username("toot")
                .password("toot1234")
                .email("toot@test.com")
                .firstName("Doe")
                .lastName("John")
                .build();
        User userToFollow = userRepository.save(user);
        UserDetailsImpl userDetailsFollower = new UserDetailsImpl(userToFollow);

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("postId", String.valueOf(postFromDb.getId()))
                        .param("userId", String.valueOf(userToFollow.getId()))
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsFollower)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.followers.length()").value(1))
                .andExpect(jsonPath("$.followers[0].username").value(userToFollow.getUsername()));
    }

    @Test
    void givenAuthorizedUserAlreadyLikedAPost_whenDislikingPost_thenReturnSuccess() throws Exception {
        PostRequestDto post = PostRequestDto.builder()
                .userId(savedUser.getId())
                .topicId(savedTopic.getId())
                .content("New post")
                .title("Title of new post")
                .build();
        Post postFromDb = postRepository.save(modelMapper.map(post, Post.class));

        User user = User.builder()
                .username("toot")
                .password("toot1234")
                .email("toot@test.com")
                .firstName("Doe")
                .lastName("John")
                .build();
        User userToLike = userRepository.save(user);
        UserDetailsImpl userDetailsLiker = new UserDetailsImpl(userToLike);

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/like")
                .contentType(MediaType.APPLICATION_JSON)
                .param("postId", String.valueOf(postFromDb.getId()))
                .param("userId", String.valueOf(userToLike.getId()))
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsLiker)));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("postId", String.valueOf(postFromDb.getId()))
                        .param("userId", String.valueOf(userToLike.getId()))
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsLiker)))
                .andExpect(jsonPath("$.status").value("Post unliked successfully!"));
    }

    @Test
    void givenAuthorizedUserAlreadyFollowedAPost_whenUnfollowingPost_thenReturnSuccess() throws Exception {
        PostRequestDto post = PostRequestDto.builder()
                .userId(savedUser.getId())
                .topicId(savedTopic.getId())
                .content("New post")
                .title("Title of new post")
                .build();
        Post postFromDb = postRepository.save(modelMapper.map(post, Post.class));

        User user = User.builder()
                .username("toot")
                .password("toot1234")
                .email("toot@test.com")
                .firstName("Doe")
                .lastName("John")
                .build();
        User userToFollow = userRepository.save(user);
        UserDetailsImpl userDetailsFollower = new UserDetailsImpl(userToFollow);

        //when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .param("postId", String.valueOf(postFromDb.getId()))
                .param("userId", String.valueOf(userToFollow.getId()))
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsFollower)));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("postId", String.valueOf(postFromDb.getId()))
                        .param("userId", String.valueOf(userToFollow.getId()))
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsFollower)))
                .andExpect(jsonPath("$.status").value("Post unfollowed successfully!"));
    }
}

package com.construction_worker_forum_back.integration;

import com.construction_worker_forum_back.config.security.JwtTokenUtil;
import com.construction_worker_forum_back.model.dto.CommentRequestDto;
import com.construction_worker_forum_back.model.entity.Comment;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CommentControllerTests extends TestcontainersConfig {

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
    private Post savedPost;

    CommentControllerTests() {
    }

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

    private void setupPost() {
        Post post = Post.builder()
                .user(savedUser)
                .topic(savedTopic)
                .title("Building cranes")
                .content("A crane is a type of machine, generally equipped with a hoist rope.")
                .build();

        savedPost = postRepository.save(post);
    }

    @BeforeEach
    void setUp() {
        removeService.removeAll();
        setupUser();
        setupTopic();
        setupPost();
    }

    @Test
    void givenAuthenticatedUser_whenCreatingComment_thenReturnStatusCreated() throws Exception {
        //given
        CommentRequestDto comment = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post")
                .build();

        //when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails))
                .content(objectMapper.writeValueAsString(comment)));

        //then
        response.andDo(print())
                .andExpect(status().isCreated());

    }

    @Test
    void givenUnauthenticatedUser_whenCreatingComment_thenReturnStatusUnauthorized() throws Exception {
        //given
        CommentRequestDto comment = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post")
                .build();

        //when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)));

        //then
        response.andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAuthenticatedUser_whenCreatingSubComment_thenReturnStatusCreated() throws Exception {
        //given
        Comment primaryComment = Comment.builder()
                .user(savedUser)
                .post(savedPost)
                .content("nice post")
                .build();
        Comment savedComment = commentRepository.save(primaryComment);
        Long primaryCommentId = savedComment.getId();

        CommentRequestDto subComment = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post")
                .build();

        //when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails))
                .param("commentForReplyId", String.valueOf(primaryCommentId))
                .content(objectMapper.writeValueAsString(subComment)));

        //then
        response.andDo(print())
                .andExpect(status().isCreated());

    }

    @Test
    void givenUnauthenticatedUser_whenCreatingSubComment_thenReturnStatusUnauthorized() throws Exception {
        //given
        Comment primaryComment = Comment.builder()
                .user(savedUser)
                .post(savedPost)
                .content("nice post")
                .build();
        Comment savedComment = commentRepository.save(primaryComment);
        Long primaryCommentId = savedComment.getId();

        CommentRequestDto subComment = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post")
                .build();

        //when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .param("commentForReplyId", String.valueOf(primaryCommentId))
                .content(objectMapper.writeValueAsString(subComment)));

        //then
        response.andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAuthenticatedUser_whenDeletingHisOwnComment_thenSuccess() throws Exception {

        //given
        Comment comment = Comment.builder()
                .user(savedUser)
                .post(savedPost)
                .content("nice post")
                .build();
        Comment savedComment = commentRepository.save(comment);
        Long id = savedComment.getId();

        //when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails))
                .content(objectMapper.writeValueAsString(comment))
                .param("userId", String.valueOf(savedUser.getId())));

        //then
        response
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void givenAuthenticatedUser_whenDeletingNotHisOwnComment_thenFail() throws Exception {

        //given
        Comment comment = Comment.builder()
                .user(savedUser)
                .post(savedPost)
                .content("nice post")
                .build();
        Comment savedComment = commentRepository.save(comment);

        User user = User.builder()
                .username("user2")
                .password("password2")
                .email("userunique2@example.com")
                .userRoles(Role.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        User savedUser2 = userRepository.save(user);

        UserDetailsImpl userDetails2 = new UserDetailsImpl(savedUser2);

        Long id = savedComment.getId();

        //when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails2))
                .content(objectMapper.writeValueAsString(comment))
                .param("userId", String.valueOf(savedUser2.getId())));

        //then
        response
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUserWhoOwnsThePost_whenEditingComment_thenSuccess() throws Exception {

        //given
        CommentRequestDto comment = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post")
                .build();

        Comment savedComment = commentRepository.save(modelMapper.map(comment, Comment.class));

        CommentRequestDto editedComment = CommentRequestDto.builder()
                .content("Changed title")
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .build();

        Long id = savedComment.getId();
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/comment/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails))
                .content(objectMapper.writeValueAsString(editedComment)));

        response
                .andDo(print())
                .andExpect(jsonPath("$.content").value("Changed title"));
    }


    @Test
    void givenAuthorizedUser_WhenHeLikesAComment_ThenSuccess() throws Exception {
        CommentRequestDto comment = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post")
                .build();

        Comment savedComment = commentRepository.save(modelMapper.map(comment, Comment.class));

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/like")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails))
                .param("commentId", String.valueOf(savedComment.getId()))
                .param("userId", String.valueOf(savedUser.getId())));
        response
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.likers").isNotEmpty())
                .andExpect(jsonPath("$.likers[0].id").value(savedUser.getId()))
                .andExpect(jsonPath("$.likers.length()").value(1));
    }

    @Test
    void givenAuthorizedUser_WhenTheyLikeACommentThatHeAlreadyLiked_ThenReturnConflict() throws Exception {
        CommentRequestDto comment = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post")
                .build();

        Comment savedComment = commentRepository.save(modelMapper.map(comment, Comment.class));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/comment/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails))
                        .param("commentId", String.valueOf(savedComment.getId()))
                        .param("userId", String.valueOf(savedUser.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.likers").isNotEmpty())
                .andExpect(jsonPath("$.likers[0].id").value(savedUser.getId()))
                .andExpect(jsonPath("$.likers.length()").value(1));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/comment/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails))
                        .param("commentId", String.valueOf(savedComment.getId()))
                        .param("userId", String.valueOf(savedUser.getId())))
                .andExpect(status().isConflict());
    }

    @Test
    void givenTwoAuthenticatedUsers_WhenTheyLikeAComment_ThenSuccess() throws Exception {
        CommentRequestDto comment = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post")
                .build();

        Comment savedComment = commentRepository.save(modelMapper.map(comment, Comment.class));

        User userTwo = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .password("qwerty12345")
                .email("email@test.com")
                .build();

        User savedUser2 = userRepository.save(userTwo);

        UserDetailsImpl userDetails2 = new UserDetailsImpl(userTwo);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/comment/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails))
                        .param("commentId", String.valueOf(savedComment.getId()))
                        .param("userId", String.valueOf(savedUser.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.likers").isNotEmpty())
                .andExpect(jsonPath("$.likers[0].id").value(savedUser.getId()))
                .andExpect(jsonPath("$.likers.length()").value(1));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/comment/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetails2))
                        .param("commentId", String.valueOf(savedComment.getId()))
                        .param("userId", String.valueOf(savedUser2.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.likers").isNotEmpty())
                .andExpect(jsonPath("$.likers.length()").value(2));
    }

    @Test
    void givenUserHasRoleAdministrator_whenRequestingAllComments_ThenReturnsListOfAllComments() throws Exception {
        CommentRequestDto comment = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post")
                .build();

        Comment savedComment = commentRepository.save(modelMapper.map(comment, Comment.class));

        CommentRequestDto comment2 = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post2")
                .build();

        Comment savedComment2 = commentRepository.save(modelMapper.map(comment2, Comment.class));

        User admin = User.builder()
                .firstName("Admin")
                .lastName("Adminowsky")
                .email("root@test.com")
                .username("root")
                .password("qwerty1234")
                .userRoles(Role.ADMINISTRATOR)
                .build();
        User savedAdmin = userRepository.save(admin);
        UserDetailsImpl userDetailsAdmin = new UserDetailsImpl(savedAdmin);

        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/comment/")
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsAdmin)))
                .andExpect(jsonPath("$[0].content").value(comment.getContent()));

    }

    @Test
    void givenUserHasRoleUser_whenRequestingAllComments_ThenReturnsNOPE() throws Exception {
        CommentRequestDto comment = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post")
                .build();

        Comment savedComment = commentRepository.save(modelMapper.map(comment, Comment.class));

        CommentRequestDto comment2 = CommentRequestDto.builder()
                .postId(savedPost.getId())
                .userId(savedUser.getId())
                .content("Nice post2")
                .build();

        Comment savedComment2 = commentRepository.save(modelMapper.map(comment2, Comment.class));

        User admin = User.builder()
                .firstName("Admin")
                .lastName("Adminowsky")
                .email("root@test.com")
                .username("root")
                .password("qwerty1234")
                .userRoles(Role.USER)
                .build();
        User savedAdmin = userRepository.save(admin);
        UserDetailsImpl userDetailsAdmin = new UserDetailsImpl(savedAdmin);

        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/comment/")
                        .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsAdmin)))
                .andExpect(status().isForbidden());

    }
}

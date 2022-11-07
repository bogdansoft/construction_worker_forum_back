package com.construction_worker_forum_back.integration;

import com.construction_worker_forum_back.config.security.JwtTokenUtil;
import com.construction_worker_forum_back.model.dto.CommentRequestDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
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


    @BeforeEach
    void setUp() {
        removeService.removeAll();
    }

    @Test
    void () throws Exception {

        //given
        User user = User.builder()
                .username("user")
                .password("password2")
                .email("userunique@example.com")
                .userRoles(Role.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        User savedUser = userRepository.save(user);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Topic topic = Topic.builder()
                .user(user)
                .name("Tower cranes")
                .description("Test description")
                .build();

        topicRepository.save(topic);

        Post post = Post.builder()
                .user(savedUser)
                .topic(topic)
                .title("Building cranes")
                .content("A crane is a type of machine, generally equipped with a hoist rope.")
                .build();

        Post savedPost = postRepository.save(post);

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
    void canGetAllComments() {


    }

}

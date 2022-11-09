package com.construction_worker_forum_back.integration;

import com.construction_worker_forum_back.config.security.JwtTokenUtil;
import com.construction_worker_forum_back.model.dto.TopicDto;
import com.construction_worker_forum_back.model.dto.TopicRequestDto;
import com.construction_worker_forum_back.model.entity.Topic;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.model.security.AccountStatus;
import com.construction_worker_forum_back.model.security.Role;
import com.construction_worker_forum_back.model.security.UserDetailsImpl;
import com.construction_worker_forum_back.repository.TopicRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Slf4j
public class TopicControllerTests extends TestcontainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenUtil tokenUtil;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RemoveService removeService;

    private UserDetailsImpl userDetailsForAdmin;
    private UserDetailsImpl userDetailsForUser;
    private User savedAdmin;
    private User savedUser;

    public TopicControllerTests() {
    }

    private void setupUsers() {
        User admin = User.builder()
                .username("admin")
                .password("password2")
                .email("userunique@example.com")
                .userRoles(Role.ADMINISTRATOR)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        User user = User.builder()
                .username("user")
                .password("password2")
                .email("userunique1@example.com")
                .userRoles(Role.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        savedAdmin = userRepository.save(admin);
        savedUser = userRepository.save(user);

        userDetailsForAdmin = new UserDetailsImpl(admin);
        userDetailsForUser = new UserDetailsImpl(user);
    }

    @BeforeEach
    void setUp() {
        removeService.removeAll();
        setupUsers();
    }

    @Test
    void givenAuthorizedAdmin_whenCreatingTopic_thenReturnStatusCreated() throws Exception {
        //Given
        TopicRequestDto topicRequestDto = TopicRequestDto.builder()
                .name("foo")
                .description("This is new topic")
                .userId(savedAdmin.getId())
                .build();

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsForAdmin))
                .content(objectMapper.writeValueAsString(topicRequestDto)));

        //Then
        response.andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void givenAuthorizedUser_whenCreatingTopic_thenReturnStatusForbidden() throws Exception {
        //Given
        TopicRequestDto topicRequestDto = TopicRequestDto.builder()
                .name("foo")
                .description("This is new topic")
                .userId(savedAdmin.getId())
                .build();

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsForUser))
                .content(objectMapper.writeValueAsString(topicRequestDto)));

        //Then
        response.andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthorizedUser_whenCreatingTopic_thenReturnStatusUnauthorized() throws Exception {
        //Given
        TopicRequestDto topicRequestDto = TopicRequestDto.builder()
                .name("foo")
                .description("This is new topic")
                .userId(savedAdmin.getId())
                .build();

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(topicRequestDto)));

        //Then
        response.andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAuthorizedAdmin_whenDeletingTopic_thenSuccess() throws Exception {
        //Given
        Topic topic = Topic.builder()
                .name("foo")
                .description("This is new topic")
                .user(savedUser)
                .build();

        Topic savedTopic = topicRepository.save(topic);
        Long id = savedTopic.getId();

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/topic/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsForAdmin))
                .content(objectMapper.writeValueAsString(topic)));

        //Then
        response
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void givenAuthorizedUser_whenDeletingTopic_thenStatusForbidden() throws Exception {
        //Given
        Topic topic = Topic.builder()
                .name("foo")
                .description("This is new topic")
                .user(savedUser)
                .build();

        Topic savedTopic = topicRepository.save(topic);
        Long id = savedTopic.getId();

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/topic/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsForUser))
                .content(objectMapper.writeValueAsString(topic)));

        //Then
        response
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthorizedUser_whenDeletingTopic_thenStatusUnauthorized() throws Exception {
        //Given
        Topic topic = Topic.builder()
                .name("foo")
                .description("This is new topic")
                .user(savedUser)
                .build();

        Topic savedTopic = topicRepository.save(topic);
        Long id = savedTopic.getId();

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/topic/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(topic)));

        //Then
        response
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAuthorizedAdmin_whenEditingTopic_thenStatusSuccess() throws Exception {
        //Given
        TopicRequestDto topicRequestDto = TopicRequestDto.builder()
                .name("foo")
                .description("This is new topic")
                .userId(savedAdmin.getId())
                .build();

        Topic savedTopic = topicRepository.save(modelMapper.map(topicRequestDto, Topic.class));

        TopicRequestDto editedTopicRequestDto = TopicRequestDto.builder()
                .name("ChangedName")
                .description("This is edited topic")
                .userId(savedAdmin.getId())
                .build();

        Long id = savedTopic.getId();

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/topic/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsForAdmin))
                .content(objectMapper.writeValueAsString(editedTopicRequestDto)));

        //Then
        response
                .andDo(print())
                .andExpect(jsonPath("$.name").value("ChangedName"));
    }

    @Test
    void givenAuthorizedUser_whenEditingTopic_thenStatusForbidden() throws Exception {
        //Given
        TopicRequestDto topicRequestDto = TopicRequestDto.builder()
                .name("foo")
                .description("This is new topic")
                .userId(savedAdmin.getId())
                .build();

        Topic savedTopic = topicRepository.save(modelMapper.map(topicRequestDto, Topic.class));

        TopicRequestDto editedTopicRequestDto = TopicRequestDto.builder()
                .name("ChangedName")
                .description("This is edited topic")
                .userId(savedAdmin.getId())
                .build();

        Long id = savedTopic.getId();

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/topic/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenUtil.generateToken(userDetailsForUser))
                .content(objectMapper.writeValueAsString(editedTopicRequestDto)));

        //Then
        response
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthorizedUser_whenEditingTopic_thenStatusUnauthorized() throws Exception {
        //Given
        TopicRequestDto topicRequestDto = TopicRequestDto.builder()
                .name("foo")
                .description("This is new topic")
                .userId(savedAdmin.getId())
                .build();

        Topic savedTopic = topicRepository.save(modelMapper.map(topicRequestDto, Topic.class));

        TopicRequestDto editedTopicRequestDto = TopicRequestDto.builder()
                .name("ChangedName")
                .description("This is edited topic")
                .userId(savedAdmin.getId())
                .build();

        Long id = savedTopic.getId();

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/topic/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editedTopicRequestDto)));

        //Then
        response
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenUnauthorizedUser_whenFetchingAllTopics_thenStatusOk() throws Exception {
        List<Topic> topics = new ArrayList<>(List.of(
                Topic.builder()
                        .name("foo")
                        .description("This is example topic")
                        .build(),
                Topic.builder()
                        .name("foo2")
                        .description("This is example topic2")
                        .build()
                ));

        var savedTopics = topicRepository.saveAll(topics);

        List<TopicDto> topicDtos = savedTopics.stream()
                .map(topic -> modelMapper.map(topic, TopicDto.class))
                .toList();

        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/topic/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(topicDtos)));

        response
                .andDo(print())
                .andExpect(status().isOk());
    }
}

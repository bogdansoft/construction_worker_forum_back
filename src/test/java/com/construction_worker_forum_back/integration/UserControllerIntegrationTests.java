package com.construction_worker_forum_back.integration;

import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.model.security.AccountStatus;
import com.construction_worker_forum_back.model.security.Role;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
class UserControllerIntegrationTests extends TestcontainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RemoveService removeService;

    @Autowired
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private static User admin;
    private static User support;
    private static User user;

    private static void setUpAdmin() {
        admin = User.builder()
                .username("admin")
                .password("password")
                .email("yoda@example.com")
                .userRoles(Role.ADMINISTRATOR)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    private static void setUpSupport() {
        support = User.builder()
                .username("support")
                .password("password")
                .email("windu@example.com")
                .userRoles(Role.SUPPORT)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    private static void setUpUser() {
        user = User.builder()
                .username("user")
                .password("password")
                .email("padawan@example.com")
                .userRoles(Role.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    @BeforeAll
    static void beforeAll() {
        setUpAdmin();
        setUpSupport();
        setUpUser();
    }

    @BeforeEach
    void setup() {
        removeService.removeAll();
        userRepository.save(admin);
        userRepository.save(support);
        userRepository.save(user);
    }

    @Test
    void givenUser_whenCreated_thenReturnSavedUser() throws Exception {

        // given
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("user123")
                .password("secret")
                .email("johndoe@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        // when
        ResultActions response = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)));

        // then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id",
                        is(notNullValue())))
                .andExpect(jsonPath("$.username",
                        is(userRequestDto.getUsername())))
                .andExpect(jsonPath("$.email",
                        is(userRequestDto.getEmail())))
                .andExpect(jsonPath("$.firstName",
                        is(userRequestDto.getFirstName())))
                .andExpect(jsonPath("$.lastName",
                        is(userRequestDto.getLastName())))
                .andExpect(jsonPath("$.createdAt",
                        is(notNullValue())))
                .andExpect(jsonPath("$.accountStatus",
                        is("ACTIVE")))
                .andExpect(jsonPath("$.userRoles",
                        is("USER")));
    }

    @Test
    void givenUserWithDuplicatedEmail_whenCreated_thenReturn409() throws Exception {

        // given
        UserRequestDto alreadySavedUser = UserRequestDto.builder()
                .username("user1")
                .password("secret")
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        userService.register(alreadySavedUser);

        UserRequestDto newUserWithDuplicatedEmail = UserRequestDto.builder()
                .username("user2")
                .password("secret")
                .email("user@example.com")
                .firstName("Winston")
                .lastName("Wolf")
                .build();

        // when
        ResultActions response = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUserWithDuplicatedEmail)));

        // then
        response.andDo(print()).
                andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "admin")
    void givenSavedUser_whenSearchedById_thenReturnFoundUser() throws Exception {

        // given
        UserRequestDto userToSave = UserRequestDto.builder()
                .username("test")
                .password("secret")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        int id = userService.register(userToSave)
                .orElseThrow()
                .getId()
                .intValue();

        // when
        ResultActions response = mockMvc
                .perform(get("/api/user/{id}", id));

        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id",
                        is(id)))
                .andExpect(jsonPath("$.username",
                        is(userToSave.getUsername())))
                .andExpect(jsonPath("$.email",
                        is(userToSave.getEmail())))
                .andExpect(jsonPath("$.firstName",
                        is(userToSave.getFirstName())))
                .andExpect(jsonPath("$.lastName",
                        is(userToSave.getLastName())))
                .andExpect(jsonPath("$.createdAt",
                        is(notNullValue())))
                .andExpect(jsonPath("$.accountStatus",
                        is("ACTIVE")))
                .andExpect(jsonPath("$.userRoles",
                        is("USER")));
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "admin")
    void givenSavedUsers_whenAllRetrievedByAdmin_thenReturnListOfUsers() throws Exception {

        // given
        UserRequestDto user1 = UserRequestDto.builder()
                .username("user1")
                .password("secret")
                .email("user1@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        UserRequestDto user2 = UserRequestDto.builder()
                .username("user2")
                .password("secret")
                .email("user2@example.com")
                .firstName("Winston")
                .lastName("Wolf")
                .build();

        userService.register(user1);
        userService.register(user2);

        // when
        ResultActions response = mockMvc.perform(get("/api/user"));

        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()",
                        is(5)));
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION, value = "support")
    void givenSavedUsers_whenAllRetrievedBySupport_thenReturnListOfUsers() throws Exception {

        // given
        UserRequestDto user1 = UserRequestDto.builder()
                .username("user1")
                .password("secret")
                .email("user1@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        UserRequestDto user2 = UserRequestDto.builder()
                .username("user2")
                .password("secret")
                .email("user2@example.com")
                .firstName("Winston")
                .lastName("Wolf")
                .build();

        userService.register(user1);
        userService.register(user2);

        // when
        ResultActions response = mockMvc.perform(get("/api/user"));

        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()",
                        is(5)));
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void givenSavedUsers_whenAllRetrievedByUser_thenReturn403() throws Exception {

        // given
        UserRequestDto user1 = UserRequestDto.builder()
                .username("user1")
                .password("secret")
                .email("user1@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        UserRequestDto user2 = UserRequestDto.builder()
                .username("user2")
                .password("secret")
                .email("user2@example.com")
                .firstName("Winston")
                .lastName("Wolf")
                .build();

        userService.register(user1);
        userService.register(user2);

        // when
        ResultActions response = mockMvc.perform(get("/api/user"));

        // then
        response.andDo(print())
                .andExpect(status().isForbidden());
    }
}

package com.construction_worker_forum_back.integration;

import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.model.security.AccountStatus;
import com.construction_worker_forum_back.model.security.Role;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
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

    private void setupUser() {
        User admin = User.builder()
                .username("admin")
                .password("password1")
                .email("admin@example.com")
                .userRoles(Role.ADMINISTRATOR)
                .build();

        User user = User.builder()
                .username("user")
                .password("password2")
                .email("userunique@example.com")
                .userRoles(Role.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        userRepository.save(admin);
        userRepository.save(user);
    }

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

    @BeforeEach
    void setUp() {
        removeService.removeAll();
        setupUser();
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
    @WithUserDetails("admin")
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
        ResultActions response = mockMvc.perform(get("/api/user/{id}", id));

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
}

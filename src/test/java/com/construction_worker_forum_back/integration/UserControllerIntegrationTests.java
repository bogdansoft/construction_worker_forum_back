package com.construction_worker_forum_back.integration;

import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class UserControllerIntegrationTests extends TestcontainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
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
        response.andDo(print()).
                andExpect(status().isCreated())
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
                        is("CREATED")))
                .andExpect(jsonPath("$.userRoles",
                        is("USER")));
    }
}

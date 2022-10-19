package com.construction_worker_forum_back.integration;

import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceIntegrationTests extends TestcontainersConfig {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RemoveService removeService;

    @BeforeEach
    void setUp() {
        removeService.removeAll();
    }

    @Test
    void saveUser() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("jake")
                .password("secret")
                .email("jake@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        userService.register(userRequestDto);


        assertTrue(userRepository.existsByUsernameIgnoreCase(userRequestDto.getUsername()));
    }
}

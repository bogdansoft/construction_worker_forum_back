package com.construction_worker_forum_back;

import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConstructionWorkerForumBackApplicationTests {

    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    void saveUser() {
        User user = User.builder()
                .username("user1234")
                .password("secret")
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        userService.register(user);
    }
}

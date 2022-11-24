package com.construction_worker_forum_back.productivity;

import com.construction_worker_forum_back.integration.RemoveService;
import com.construction_worker_forum_back.integration.TestcontainersConfig;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.config.repository.UserRepository;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@TestExecutionListeners(listeners = { TestProductivityExecutionListener.class }, mergeMode = MERGE_WITH_DEFAULTS)
public class UserProductivityServiceTest extends TestcontainersConfig {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RemoveService removeService;

    @Autowired
    public UserProductivityServiceTest(UserService userService, UserRepository userRepository, RemoveService removeService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.removeService = removeService;
    }

    @BeforeEach
    void setUp() {
        removeService.removeAll();
    }

    @Test
    public void productivityUserSavingTest() {
        //given
        UserRequestDto userRequestDto;

        for (int i = 1; i <= 100; i++) {
            userRequestDto = UserRequestDto.builder()
                    .username("jake" + i)
                    .password("secret" + i)
                    .email("jake" + i + "@example.com")
                    .firstName("John" + i)
                    .lastName("Doe" + i)
                    .build();

            //when
            userService.register(userRequestDto);
        }

        //then
        assertEquals(100, userRepository.findAll().size());
    }
}

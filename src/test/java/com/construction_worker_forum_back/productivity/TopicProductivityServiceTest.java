package com.construction_worker_forum_back.productivity;

import com.construction_worker_forum_back.integration.RemoveService;
import com.construction_worker_forum_back.integration.TestcontainersConfig;
import com.construction_worker_forum_back.model.dto.TopicRequestDto;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.repository.TopicRepository;
import com.construction_worker_forum_back.service.TopicService;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@TestExecutionListeners(listeners = { TestProductivityExecutionListener.class }, mergeMode = MERGE_WITH_DEFAULTS)
public class TopicProductivityServiceTest extends TestcontainersConfig {

    private final TopicService topicService;
    private final TopicRepository topicRepository;
    private final RemoveService removeService;
    private final UserService userService;

    @Autowired
    public TopicProductivityServiceTest(TopicService topicService, TopicRepository topicRepository, RemoveService removeService, UserService userService) {
        this.topicService = topicService;
        this.topicRepository = topicRepository;
        this.removeService = removeService;
        this.userService = userService;
    }

    @BeforeEach
    void setUp() {
        removeService.removeAll();
    }

    @Test
    public void productivityTopicSavingTest() {
        //given
        TopicRequestDto topicRequestDto;
        UserRequestDto userRequestDto;

        userRequestDto = UserRequestDto.builder()
                .username("jake")
                .password("secret")
                .email("jake@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        Long userId = userService.register(userRequestDto).get().getId();

        for (int i = 1; i <= 100; i++) {
            topicRequestDto = TopicRequestDto.builder()
                    .name("TestTest")
                    .description("TestTest")
                    .userId(userId)
                    .build();

            //when
            topicService.createTopic(topicRequestDto);
        }

        //then
        assertEquals(100, topicRepository.findAll().size());
    }
}

package com.construction_worker_forum_back.productivity;

import com.construction_worker_forum_back.integration.RemoveService;
import com.construction_worker_forum_back.integration.TestcontainersConfig;
import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.dto.TopicRequestDto;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.repository.PostRepository;
import com.construction_worker_forum_back.service.PostService;
import com.construction_worker_forum_back.service.TopicService;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@TestExecutionListeners(listeners = { TestProductivityExecutionListener.class }, mergeMode = MERGE_WITH_DEFAULTS)
public class PostProductivityServiceTest extends TestcontainersConfig {

    private final PostService postService;
    private final PostRepository postRepository;
    private final UserService userService;
    private final TopicService topicService;
    private final RemoveService removeService;

    @Autowired
    public PostProductivityServiceTest(PostService postService, PostRepository postRepository, RemoveService removeService, UserService userService, TopicService topicService) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.removeService = removeService;
        this.topicService = topicService;
        this.userService = userService;
    }

    @BeforeEach
    void setUp() {
        removeService.removeAll();
    }

    @Test
    public void productivityUserSavingTest() {
        //given
        PostRequestDto postRequestDto;
        UserRequestDto userRequestDto;
        TopicRequestDto topicRequestDto;

        userRequestDto = UserRequestDto.builder()
                .username("jake")
                .password("secret")
                .email("jake@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        Long userId = userService.register(userRequestDto).get().getId();

        topicRequestDto = TopicRequestDto.builder()
                .name("Testtesttets")
                .description("TestTestTest")
                .userId(userId)
                .build();
        Long topicId = topicService.createTopic(topicRequestDto).getId();


        for (int i = 1; i <= 100; i++) {
            postRequestDto = PostRequestDto.builder()
                    .userId(userId)
                    .content("secret" + i)
                    .title("jake" + i + "@example.com")
                    .topicId(topicId)
                    .build();

            //when
            postService.createPost(postRequestDto);
        }

        //then
        assertEquals(100, postRepository.findAll().size());
    }
}

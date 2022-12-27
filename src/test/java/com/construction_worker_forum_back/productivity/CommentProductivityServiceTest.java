package com.construction_worker_forum_back.productivity;

import com.construction_worker_forum_back.integration.RemoveService;
import com.construction_worker_forum_back.integration.TestcontainersConfig;
import com.construction_worker_forum_back.model.dto.CommentRequestDto;
import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.dto.TopicRequestDto;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.repository.CommentRepository;
import com.construction_worker_forum_back.service.CommentService;
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
public class CommentProductivityServiceTest extends TestcontainersConfig {

    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final RemoveService removeService;
    private final UserService userService;
    private final PostService postService;
    private final TopicService topicService;

    @Autowired
    public CommentProductivityServiceTest(CommentService commentService, CommentRepository commentRepository, RemoveService removeService, UserService userService, PostService postService, TopicService topicService) {
        this.commentService = commentService;
        this.commentRepository = commentRepository;
        this.removeService = removeService;
        this.userService = userService;
        this.postService = postService;
        this.topicService = topicService;
    }

    @BeforeEach
    void setUp() {
        removeService.removeAll();
    }

    @Test
    public void productivityCommentSavingTest() {
        CommentRequestDto commentRequestDto;
        UserRequestDto userRequestDto;
        PostRequestDto postRequestDto;
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

        postRequestDto = PostRequestDto.builder()
                .userId(userId)
                .content("TestTestTest")
                .title("TestTestTest")
                .topicId(topicId)
                .build();
        Long postId = postService.createPost(postRequestDto).getId();

        for (int i = 1; i <= 100; i++) {
            commentRequestDto = CommentRequestDto.builder()
                    .content("jake" + i)
                    .postId(postId)
                    .userId(userId)
                    .build();

            //when
            commentService.createComment(commentRequestDto, null);
        }

        //then
        assertEquals(100, commentRepository.findAll().size());
    }
}

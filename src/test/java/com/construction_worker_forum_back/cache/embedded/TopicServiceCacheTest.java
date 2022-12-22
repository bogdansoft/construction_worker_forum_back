package com.construction_worker_forum_back.cache.embedded;

import com.construction_worker_forum_back.config.redis.RedisConfig;
import com.construction_worker_forum_back.model.dto.TopicDto;
import com.construction_worker_forum_back.model.entity.Topic;
import com.construction_worker_forum_back.repository.TopicRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.TopicService;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@Import({RedisConfig.class, TopicService.class})
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class,
        EmbeddedRedisConfiguration.class
})
@EnableCaching
@TestExecutionListeners(listeners = { EmbeddedRedisConfiguration.class }, mergeMode = MERGE_WITH_DEFAULTS)
public class TopicServiceCacheTest {

    @MockBean
    private TopicRepository topicRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private ModelMapper modelMapper;
    @Autowired
    private TopicService topicService;
    @Autowired
    private CacheManager cacheManager;

    @Test
    void itShouldGetByIdTopic() {
        //Given
        Topic topic = new Topic();
        topic.setId(100L);

        given(topicRepository.findById(topic.getId())).willReturn(Optional.of(topic));
        given(modelMapper.map(topic, TopicDto.class)).willReturn(TopicDto.builder().id(100L).build());

        //When
        topicService.findTopicById(topic.getId());
        topicService.findTopicById(topic.getId());

        //Then
        verify(topicRepository, times(1)).findById(anyLong());
    }
}

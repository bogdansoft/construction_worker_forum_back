package com.construction_worker_forum_back.cache.test_contatiners;

import com.construction_worker_forum_back.model.dto.TopicDto;
import com.construction_worker_forum_back.model.entity.Topic;
import com.construction_worker_forum_back.repository.TopicRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.TopicService;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(TopicService.class)
public class TopicServiceCacheContainerTest extends AbstractTestContainerSetUpClass {

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
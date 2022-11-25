package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.TopicDto;
import com.construction_worker_forum_back.model.dto.TopicRequestDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.simple.UserSimpleDto;
import com.construction_worker_forum_back.model.entity.Topic;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private TopicService topicService;

    @Test
    void itShouldGetAllTopics() {
        //Given
        List<Topic> topicList = new ArrayList<>(List.of(new Topic()));
        given(topicRepository.findAll()).willReturn(topicList);

        //When
        var expected = topicService.getAllTopics(Optional.empty(), Optional.empty(), Optional.empty());

        //Then
        assertEquals(expected.size(), topicList.size());
        verify(topicRepository, atLeastOnce()).findAll();
    }

    @Test
    void itShouldGetByIdTopic() {
        //Given
        Topic topic = new Topic();
        topic.setId(1L);

        given(topicRepository.findById(topic.getId())).willReturn(Optional.of(topic));
        given(modelMapper.map(topic, TopicDto.class)).willReturn(TopicDto.builder().id(1L).build());

        //When
        var expected = topicService.findTopicById(topic.getId());

        //Then
        assertTrue(expected.isPresent());
        assertTrue(expected.get().getId() > 0);
        verify(topicRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void itShouldGetByNameTopic() {
        //Given
        Topic topic = new Topic();
        topic.setName("foo");

        given(topicRepository.findTopicByName(topic.getName())).willReturn(Optional.of(topic));
        given(modelMapper.map(topic, TopicDto.class)).willReturn(TopicDto.builder().name("foo").build());

        //When
        var expected = topicService.findTopicByName(topic.getName());

        //Then
        assertTrue(expected.isPresent());
        assertEquals(expected.get().getName(), topic.getName());
        verify(topicRepository, atLeastOnce()).findTopicByName(topic.getName());
    }

    @Test
    void itShouldDeleteTopicById() {
        //Given
        Topic topic = new Topic();
        topic.setId(1L);

        given(topicRepository.deleteTopicById(topic.getId())).willReturn(1);

        //When
        var expected = topicService.deleteTopicById(topic.getId());

        //Then
        assertTrue(expected);
        verify(topicRepository, atLeastOnce()).deleteTopicById(topic.getId());
    }

    @Test
    void itShouldCreateTopic() {
        //Given
        TopicRequestDto topicRequestDto = TopicRequestDto.builder()
                .name("foo")
                .userId(1L)
                .build();

        TopicDto topicDto = TopicDto.builder()
                .name("foo")
                .user(UserSimpleDto.builder().id(1L).username("userFoo").build())
                .build();

        User user = User.builder()
                .id(1L)
                .username("userFoo")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("userFoo")
                .build();

        Topic topic = Topic.builder()
                .id(1L)
                .build();

        given(modelMapper.map(topicRequestDto, Topic.class)).willReturn(topic);
        given(modelMapper.map(userDto, User.class)).willReturn(user);
        given(modelMapper.map(topic, TopicDto.class)).willReturn(topicDto);
        given(userService.findById(topicRequestDto.getUserId())).willReturn(Optional.of(userDto));
        given(topicRepository.save(topic)).willReturn(topic);

        //When
        var expected = topicService.createTopic(topicRequestDto);

        //Then
        assertNotNull(expected);
        assertEquals(expected.getName(), topicDto.getName());
        assertEquals(expected.getUser().getId(), userDto.getId());
        assertEquals(expected.getUser().getUsername(), userDto.getUsername());

        verify(topicRepository, atLeastOnce()).save(topic);
        verify(userService, atLeastOnce()).findById(topicRequestDto.getUserId());
        verify(modelMapper, atLeastOnce()).map(topicRequestDto, Topic.class);
        verify(modelMapper, atLeastOnce()).map(userDto, User.class);
        verify(modelMapper, atLeastOnce()).map(topic, TopicDto.class);
    }

    @Test
    void itShouldUpdateTopicById() {
        //Given
        Long topicToUpdateId = 1L;

        TopicRequestDto topicRequestDto = TopicRequestDto.builder()
                .name("fooTopic")
                .description("The topic to update")
                .userId(1L)
                .build();

        User user = User.builder()
                .id(topicRequestDto.getUserId())
                .username("userFoo")
                .build();

        Topic topic = Topic.builder()
                .id(topicToUpdateId)
                .name(topicRequestDto.getName())
                .description(topicRequestDto.getDescription())
                .user(user)
                .build();

        TopicDto topicDto = TopicDto.builder()
                .id(topicToUpdateId)
                .name(topic.getName())
                .description(topic.getDescription())
                .updatedAt(Date.from(Instant.now()))
                .user(UserSimpleDto.builder()
                        .id(topic.getUser().getId())
                        .username(topic.getUser().getUsername())
                        .build())
                .build();

        given(topicRepository.findById(topicToUpdateId)).willReturn(Optional.of(topic));
        doNothing().when(modelMapper).map(topicRequestDto, topic);
        given(modelMapper.map(topic, TopicDto.class)).willReturn(topicDto);

        //When
        var expected = topicService.updateTopicById(topicToUpdateId, topicRequestDto);

        //Then
        assertNotNull(expected);
        assertEquals(expected.getName(), topicRequestDto.getName());
        assertEquals(expected.getId(), topicToUpdateId);
        assertEquals(expected.getDescription(), topicRequestDto.getDescription());
        assertEquals(expected.getUser().getId(), user.getId());
        assertEquals(expected.getUser().getUsername(), user.getUsername());
        assertNotNull(expected.getUpdatedAt());

        verify(topicRepository, atLeastOnce()).findById(topicToUpdateId);
        verify(modelMapper, atLeastOnce()).map(topicRequestDto, topic);
        verify(modelMapper, atLeastOnce()).map(topic, TopicDto.class);
    }

    @Test
    void itShouldFindAllTopicsByName() {
        //Given
        List<Topic> topics = new ArrayList<>(List.of(
                Topic.builder()
                        .name("foo1")
                        .description("test description 1")
                        .build(),
                Topic.builder()
                        .name("foo1")
                        .description("test description 2")
                        .build()
        ));

        List<TopicDto> topicDtos = new ArrayList<>(List.of(
                TopicDto.builder().name(topics.get(0).getName()).description(topics.get(0).getDescription()).build(),
                TopicDto.builder().name(topics.get(1).getName()).description(topics.get(1).getDescription()).build()
        ));

        given(modelMapper.map(topics.get(0), TopicDto.class)).willReturn(topicDtos.get(0));
        given(modelMapper.map(topics.get(1), TopicDto.class)).willReturn(topicDtos.get(1));
        given(topicRepository.findByNameContainsIgnoreCase("foo1")).willReturn(topics);

        //When
        var expected = topicService.findAllTopicsByName("foo1");

        //Then
        assertNotNull(expected);
        assertEquals(2, expected.size());
        assertEquals(expected.get(0), topicDtos.get(0));
        assertEquals(expected.get(1), topicDtos.get(1));

        verify(modelMapper, atLeastOnce()).map(topics.get(0), TopicDto.class);
        verify(modelMapper, atLeastOnce()).map(topics.get(1), TopicDto.class);
        verify(topicRepository, atLeastOnce()).findByNameContainsIgnoreCase("foo1");
    }

    @Test
    void itShouldGetDesignatedNumberOfTopics() {
        //Given
        var numberOfTopics = 3;
        var page = 2;
        var startIndex = (page-1)*numberOfTopics;

        List<Topic> topics = new ArrayList<>(List.of(
                Topic.builder()
                        .name("foo1")
                        .description("test description 1")
                        .build(),
                Topic.builder()
                        .name("foo2")
                        .description("test description 2")
                        .build()
        ));

        List<TopicDto> topicDtos = new ArrayList<>(List.of(
                TopicDto.builder().name(topics.get(0).getName()).description(topics.get(0).getDescription()).build(),
                TopicDto.builder().name(topics.get(1).getName()).description(topics.get(1).getDescription()).build()
        ));

        given(modelMapper.map(topics.get(0), TopicDto.class)).willReturn(topicDtos.get(0));
        given(modelMapper.map(topics.get(1), TopicDto.class)).willReturn(topicDtos.get(1));
        given(topicRepository.getDesignatedNumberOfTopics(numberOfTopics, startIndex)).willReturn(topics);

        //When
        var expected = topicService.getPaginatedNumberOfTopics(numberOfTopics, page);

        //Then
        assertNotNull(expected);
        assertEquals(expected.size(), 2);
        assertEquals(expected.get(0), topicDtos.get(0));
        assertEquals(expected.get(1), topicDtos.get(1));

        verify(modelMapper, atLeastOnce()).map(topics.get(0), TopicDto.class);
        verify(modelMapper, atLeastOnce()).map(topics.get(1), TopicDto.class);
        verify(topicRepository, atLeastOnce()).getDesignatedNumberOfTopics(numberOfTopics, startIndex);
    }
}

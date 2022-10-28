package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.TopicDto;
import com.construction_worker_forum_back.model.dto.TopicRequestDto;
import com.construction_worker_forum_back.model.entity.Topic;
import com.construction_worker_forum_back.repository.TopicRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final ModelMapper modelMapper;

    public List<TopicDto> getAllTopics() {
        return topicRepository
                .findAll()
                .stream()
                .map(topic -> modelMapper.map(topic, TopicDto.class))
                .collect(Collectors.toList());
    }

    public Optional<TopicDto> findTopicById(Long id) {
        return topicRepository.findById(id)
                .map(topic -> modelMapper.map(topic, TopicDto.class));
    }

    public Optional<TopicDto> findTopicByName(String name) {
        return topicRepository.findTopicByName(name)
                .map(topic -> modelMapper.map(topic, TopicDto.class));
    }

    @Transactional
    public boolean deleteTopicById(Long id) {
        return topicRepository.deleteTopicById(id) == 1;
    }

    @Transactional
    public TopicDto createTopic(TopicRequestDto topicRequestDto) {
        Topic topicToSave = modelMapper.map(topicRequestDto, Topic.class);

        return modelMapper.map(topicRepository.save(topicToSave), TopicDto.class);
    }

    @Transactional
    public TopicDto updateTopicById(Long id, TopicRequestDto topicRequestDto) {
        Topic topic = topicRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        topic.setUpdatedAt(Date.from(Instant.now()));
        modelMapper.map(topicRequestDto, topic);

        return modelMapper.map(topic, TopicDto.class);
    }
}
package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.TopicDto;
import com.construction_worker_forum_back.model.dto.TopicRequestDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.entity.Topic;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.TopicRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.validation.EntityUpdateUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
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
    private final UserRepository userRepository;
    private final UserService userService;
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
        UserDto userById = userService
                .findById(topicRequestDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        topicToSave.setUser(modelMapper.map(userById, User.class));

        return modelMapper.map(topicRepository.save(topicToSave), TopicDto.class);
    }

    @Transactional
    public TopicDto updateTopicById(Long id, TopicRequestDto topicRequestDto) {
        Topic topicFromDb = topicRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        topicFromDb.setUpdatedAt(Date.from(Instant.now()));
        EntityUpdateUtil.setEntityLastEditor(userRepository, topicFromDb, topicRequestDto.getUserId());
        modelMapper.map(topicRequestDto, topicFromDb);

        return modelMapper.map(topicFromDb, TopicDto.class);
    }

    public List<TopicDto> findAllTopicsByName(String name) {
        return topicRepository.findByNameContainsIgnoreCase(name)
                .stream()
                .map(topic -> modelMapper.map(topic, TopicDto.class))
                .collect(Collectors.toList());
    }

    public List<TopicDto> getDesignatedNumberOfTopics(Integer number, Integer page) {
        Integer startIndex = (page-1)*number;
        return topicRepository.getDesignatedNumberOfTopics(number, startIndex)
                .stream()
                .map(topic -> modelMapper.map(topic, TopicDto.class))
                .collect(Collectors.toList());
    }

    public List<TopicDto> getSortedTopics(String orderBy) {
        String[] splitted = orderBy.split("\\.");
        if(splitted[1].equalsIgnoreCase("asc")) {
            return topicRepository.findAll(Sort.by(Sort.Direction.ASC, splitted[0]))
                    .stream()
                    .map(topic -> modelMapper.map(topic, TopicDto.class))
                    .collect(Collectors.toList());
        }
        return topicRepository.findAll(Sort.by(Sort.Direction.DESC, splitted[0]))
                .stream()
                .map(topic -> modelMapper.map(topic, TopicDto.class))
                .collect(Collectors.toList());
    }

}

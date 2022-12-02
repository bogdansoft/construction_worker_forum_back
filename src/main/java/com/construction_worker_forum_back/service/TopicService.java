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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public List<TopicDto> getAllTopics(
            Optional<String> orderBy,
            Optional<Integer> limit,
            Optional<Integer> page
    ) {
        if(limit.isPresent() && page.isPresent() && orderBy.isPresent()) {
            return getPaginatedAndSortedNumberOfTopics(limit.get(), page.get(), orderBy.get());
        }
        if(orderBy.isPresent()) {
            return getSortedTopics(orderBy.get());
        }
        if(limit.isPresent() && page.isPresent()) {
            return getPaginatedNumberOfTopics(limit.get(), page.get());
        }
        return topicRepository
                .findAll()
                .stream()
                .map(topic -> modelMapper.map(topic, TopicDto.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "topicCache", key = "{#id}")
    public Optional<TopicDto> findTopicById(Long id) {
        return topicRepository.findById(id)
                .map(topic -> modelMapper.map(topic, TopicDto.class));
    }

    public Optional<TopicDto> findTopicByName(String name) {
        return topicRepository.findTopicByName(name)
                .map(topic -> modelMapper.map(topic, TopicDto.class));
    }

    @Transactional
    @CacheEvict(value = "topicCache", key = "{#id}")
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
    @CachePut(value = "topicCache", key = "{#id}")
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

    public List<TopicDto> getPaginatedNumberOfTopics(Integer number, Integer page) {
        Pageable pageWithExactNumberOfElements = PageRequest.of(page-1, number);

        return getListOfTopicsByPageableObject(pageWithExactNumberOfElements);
    }

    public List<TopicDto> getSortedTopics(String orderBy) {
        String[] splitted = orderBy.split("\\.");
        String sortBy = splitted[0];
        String direction = splitted[1];
        if(direction.equalsIgnoreCase("asc")) {
            Sort sortTopicsAscending = Sort.by(Sort.Direction.ASC, sortBy);

            return getListOfTopicsBySort(sortTopicsAscending);
        }
        Sort sortTopicsDescending = Sort.by(Sort.Direction.DESC, sortBy);

        return getListOfTopicsBySort(sortTopicsDescending);
    }

    public List<TopicDto> getListOfTopicsBySort(Sort sort) {
        return topicRepository.findAll(sort)
                .stream()
                .map(topic -> modelMapper.map(topic, TopicDto.class))
                .collect(Collectors.toList());
    }

    public List<TopicDto> getPaginatedAndSortedNumberOfTopics(Integer number, Integer page, String orderBy) {
        String[] splitted = orderBy.split("\\.");
        String sortBy = splitted[0];
        String direction = splitted[1];
        if(direction.equalsIgnoreCase("asc")) {
            Pageable paginatedAndSortedAscending= PageRequest.of(page-1, number, Sort.by(sortBy).ascending());
            return getListOfTopicsByPageableObject(paginatedAndSortedAscending);
        }
        Pageable paginatedAndSortedDescending = PageRequest.of(page-1, number, Sort.by(sortBy).descending());

        return getListOfTopicsByPageableObject(paginatedAndSortedDescending);
    }

    public List<TopicDto> getListOfTopicsByPageableObject(Pageable pageable) {
        return topicRepository.findAll(pageable)
                .stream()
                .map(topic -> modelMapper.map(topic, TopicDto.class))
                .collect(Collectors.toList());
    }

}

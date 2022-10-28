package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.TopicDto;
import com.construction_worker_forum_back.model.dto.TopicRequestDto;
import com.construction_worker_forum_back.service.TopicService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/topic")
@AllArgsConstructor
public class TopicController {

    private TopicService topicService;

    @GetMapping
    List<TopicDto> getAllTopics() {
        return topicService.getAllTopics();
    }

    @GetMapping("/{id}")
    TopicDto getTopicById(@PathVariable("id") Long id) {
        return topicService.findTopicById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/name/{name}")
    TopicDto getTopicByName(@PathVariable("name") String name) {
        return topicService.findTopicByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TopicDto createTopic(@Valid @RequestBody TopicRequestDto topicRequestDto) {
        return topicService.createTopic(topicRequestDto);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    @PutMapping("/{id}")
    TopicDto updateTopic(@Valid @RequestBody TopicRequestDto topicRequestDto, @PathVariable("id") Long id) {
        return topicService.updateTopicById(id, topicRequestDto);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPPORT')")
    @DeleteMapping("/{id}")
    Map<String, String> deleteTopic(@PathVariable("id") Long id) {
        if (topicService.deleteTopicById(id)) {
            return Map.of(
                    "ID", id + "",
                    "status", "Deleted successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}

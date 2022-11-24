package com.construction_worker_forum_back.integration;

import com.construction_worker_forum_back.config.repository.CommentRepository;
import com.construction_worker_forum_back.config.repository.PostRepository;
import com.construction_worker_forum_back.config.repository.TopicRepository;
import com.construction_worker_forum_back.config.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("dev")
@Service
@RequiredArgsConstructor
public class RemoveService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;

    @Transactional
    public void removeAll() {
        topicRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        commentRepository.deleteAll();
    }
}

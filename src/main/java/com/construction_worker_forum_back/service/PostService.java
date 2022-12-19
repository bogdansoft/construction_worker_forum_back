package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.PostRequestDto;
import com.construction_worker_forum_back.model.dto.TopicDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.simple.FollowerSimpleDto;
import com.construction_worker_forum_back.model.dto.simple.LikerSimpleDto;
import com.construction_worker_forum_back.model.entity.Keyword;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.Topic;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.PostRepository;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.validation.EntityUpdateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TopicService topicService;
    private final ModelMapper modelMapper;

    public List<PostDto> getAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    public List<PostDto> getPostsByUsername(String username) {
        return postRepository
                .findByUser_UsernameIgnoreCase(username)
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    public List<PostDto> getPostsByTopicId(
            Long topicId,
            Optional<String> orderBy,
            Optional<Integer> limit,
            Optional<Integer> page,
            List<String> keywords
    ) {
        if (limit.isPresent() && page.isPresent() && orderBy.isPresent() && keywords != null) {
            return getPaginatedAndSortedAndFilteredPosts(topicId, limit.get(), page.get(), orderBy.get(), keywords);
        }
        if (limit.isPresent() && page.isPresent() && orderBy.isPresent()) {
            return getPaginatedAndSortedNumberOfPosts(topicId, limit.get(), page.get(), orderBy.get());
        }
        if (limit.isPresent() && page.isPresent() && keywords != null) {
            return getPaginatedAndFilteredByKeywords(topicId, limit.get(), page.get(), keywords);
        }
        if (limit.isPresent() && page.isPresent()) {
            return getPaginatedNumberOfPosts(topicId, limit.get(), page.get());
        }
        return postRepository
                .findByTopic_Id(topicId)
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    public List<LikerSimpleDto> getPostLikers(Long id) {
        return postRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getLikers()
                .stream()
                .map(user -> modelMapper.map(user, LikerSimpleDto.class))
                .toList();
    }

    public List<FollowerSimpleDto> getPostFollowers(Long id) {
        return postRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getFollowers()
                .stream()
                .map(user -> modelMapper.map(user, FollowerSimpleDto.class))
                .toList();
    }

    @Cacheable(value = "postCache", key = "{#id}")
    public Optional<PostDto> findById(Long id) {
        return postRepository.findById(id)
                .map(post -> modelMapper.map(post, PostDto.class));
    }

    @Transactional
    @CachePut(value = "pageCache", key = "{postRequestDto}")
    public PostDto createPost(PostRequestDto postRequestDto) {
        Post postToSave = modelMapper.map(postRequestDto, Post.class);
        UserDto userById = userService
                .findById(postRequestDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TopicDto topicById = topicService
                .findTopicById(postRequestDto.getTopicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        postToSave.setUser(modelMapper.map(userById, User.class));
        postToSave.setTopic(modelMapper.map(topicById, Topic.class));

        return modelMapper.map(postRepository.save(postToSave), PostDto.class);
    }

    @Transactional
    @CachePut(value = "postCache", key = "{#postId}")
    public PostDto followPost(Long postId, Long userId) {
        Post postFromDb = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User userById = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (postFromDb.getFollowers().contains(userById)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Post already followed by this user!");
        }

        postFromDb.getFollowers().add(userById);
        userById.getFollowedPosts().add(postFromDb);

        return modelMapper.map(postFromDb, PostDto.class);
    }

    @Transactional
    @CachePut(value = "postCache", key = "{#postId}")
    public PostDto unfollowPost(Long postId, Long userId) {
        Post postFromDb = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User userById = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        postFromDb.getFollowers().remove(userById);
        userById.getFollowedPosts().remove(postFromDb);

        return modelMapper.map(postFromDb, PostDto.class);
    }

    @Transactional
    @CachePut(value = "postCache", key = "{#postId}")
    public PostDto likePost(Long postId, Long userId) {
        Post postFromDb = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User userById = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (postFromDb.getLikers().contains(userById)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Post already liked by user!");
        }

        postFromDb.getLikers().add(userById);
        userById.getLikedPosts().add(postFromDb);

        return modelMapper.map(postFromDb, PostDto.class);
    }

    @Transactional
    @CachePut(value = "postCache", key = "{#id}")
    public PostDto updatePostById(Long id, PostRequestDto postRequestDto) {
        Post postFromDb = postRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        EntityUpdateUtil.throwIfEditorIsUserAndTimeIsExpired(postFromDb);

        TopicDto topicById = topicService
                .findTopicById(postRequestDto.getTopicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


        EntityUpdateUtil.setEntityLastEditor(userRepository, postFromDb, postRequestDto.getUserId());
        modelMapper.map(postRequestDto, postFromDb);
        modelMapper.map(postRequestDto, topicById);
        postFromDb.setUpdatedAt(Date.from(Instant.now()));
        postFromDb.setTopic(modelMapper.map(topicById, Topic.class));
        postFromDb.setKeywords(postRequestDto.getKeywords());

        return modelMapper.map(postFromDb, PostDto.class);
    }

    @Transactional
    @CacheEvict(value = "postCache", key = "{#id}")
    public boolean deleteById(Long id) {
        return postRepository.deletePostById(id) == 1;
    }

    @Transactional
    @CachePut(value = "postCache", key = "{#postId}")
    public PostDto unlikePost(Long postId, Long userId) {
        Post postFromDb = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User userById = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        postFromDb.getLikers().remove(userById);
        userById.getLikedPosts().remove(postFromDb);

        return modelMapper.map(postFromDb, PostDto.class);
    }

    public List<PostDto> findPostByContentOrTitle(String contentOrTitle) {
        return postRepository.findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(contentOrTitle, contentOrTitle)
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();

    }

    public List<PostDto> getPaginatedNumberOfPosts(Long topicId, Integer number, Integer page) {
        Pageable pageWithExactNumberOfElements = PageRequest.of(page - 1, number);
        return getListOfPostsByPageableObject(topicId, pageWithExactNumberOfElements);
    }

    public List<PostDto> getListOfPostsByPageableObject(Long topicId, Pageable pageable) {
        return postRepository.findAllPaginatedByTopic_Id(topicId, pageable)
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(toList());
    }

    public List<PostDto> getPaginatedAndSortedNumberOfPosts(Long topicId, Integer number, Integer page, String orderBy) {
        String[] splitted = orderBy.split("\\.");
        String sortBy = splitted[0];
        String direction = splitted[1];

        if (direction.equalsIgnoreCase("asc")) {
            Pageable paginatedAndSortedAscending = PageRequest.of(page - 1, number, Sort.by(sortBy).ascending());
            return getListOfPostsByPageableObject(topicId, paginatedAndSortedAscending);
        }
        Pageable paginatedAndSortedDescending = PageRequest.of(page - 1, number, Sort.by(sortBy).descending());

        return getListOfPostsByPageableObject(topicId, paginatedAndSortedDescending);
    }

    public Set<Post> filterRecordsFromDatabaseByKeywordsToRetrieveOnlyPostsWhichHaveAllNecessaryKeywords(List<Post> posts, List<String> keywords) {
        LinkedHashSet<Post> sortedPosts = new LinkedHashSet<>();
        for (Post post : posts) {
            List<String> postKeywords = post.getKeywords().stream().map(Keyword::getName).toList();
            if (postKeywords.containsAll(keywords)) {
                sortedPosts.add(post);
            }
        }

        return sortedPosts;
    }

    public List<PostDto> getPaginatedAndFilteredByKeywords(Long topicId, Integer limit, Integer page, List<String> keywords) {
        List<Post> posts = filterRecordsFromDatabaseByKeywordsToRetrieveOnlyPostsWhichHaveAllNecessaryKeywords(
                postRepository.findAllPostsByTopicIdAndKeywords(topicId, new HashSet<>(keywords)), keywords).stream().toList();
        return getPage(posts, page, limit)
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    public List<Post> getPage(List<Post> sourceList, int page, int pageSize) {
        if (pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if (sourceList == null || sourceList.size() <= fromIndex) {
            return Collections.emptyList();
        }

        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }

    public List<PostDto> getPaginatedAndSortedAndFilteredPosts(Long topicId, Integer limit, Integer page, String orderBy, List<String> keywords) {
        List<Post> posts;
        String[] splitted = orderBy.split("\\.");

        String sortBy = splitted[0];
        String direction = splitted[1];

        if (direction.equalsIgnoreCase("asc")) {
            Sort sortTopicsAscending = Sort.by(Sort.Direction.ASC, sortBy);

            posts = filterRecordsFromDatabaseByKeywordsToRetrieveOnlyPostsWhichHaveAllNecessaryKeywords(
                    postRepository.findAllSortedPostsByTopicIdAndKeywords(topicId, new HashSet<>(keywords), sortTopicsAscending), keywords).stream().toList();

        } else {
            Sort sortTopicsDescending = Sort.by(Sort.Direction.DESC, sortBy);
            posts = filterRecordsFromDatabaseByKeywordsToRetrieveOnlyPostsWhichHaveAllNecessaryKeywords(
                    postRepository.findAllSortedPostsByTopicIdAndKeywords(topicId, new HashSet<>(keywords), sortTopicsDescending), keywords).stream().toList();
        }

        return getPage(posts, page, limit)
                .stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }
}
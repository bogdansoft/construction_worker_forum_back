package com.construction_worker_forum_back.cache.test_contatiners;

import com.amazonaws.services.s3.AmazonS3Client;
import com.construction_worker_forum_back.config.redis.RedisConfig;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.repository.UserRepository;
import com.construction_worker_forum_back.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import({RedisConfig.class, UserService.class})
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(classes = CacheAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching
public class UserServiceCacheContainerTest extends AbstractTestContainerSetUpClass {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AmazonS3Client s3Client;
    @Autowired
    private UserService userService;
    @Autowired
    private CacheManager cacheManager;

    @Test
    void givenRedisCaching_whenFindUserById_thenUserReturnedFromCache() {
        //Given
        User user = new User();
        user.setId(100L);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(modelMapper.map(user, UserDto.class)).willReturn(any());

        //When
        userService.findById(user.getId());
        userService.findById(user.getId());

        //Then
        verify(userRepository, times(1)).findById(anyLong());
    }
}
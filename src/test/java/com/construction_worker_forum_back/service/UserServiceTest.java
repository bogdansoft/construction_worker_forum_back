package com.construction_worker_forum_back.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.construction_worker_forum_back.model.dto.PostDto;
import com.construction_worker_forum_back.model.dto.UserDto;
import com.construction_worker_forum_back.model.dto.UserRequestDto;
import com.construction_worker_forum_back.model.dto.simple.BioSimpleDto;
import com.construction_worker_forum_back.model.entity.Post;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.model.security.AccountStatus;
import com.construction_worker_forum_back.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AmazonS3Client s3Client;

    @Value("${application.bucket.name}")
    private String bucketName;

    @InjectMocks
    private UserService userService;


    @Test
    void itShouldGetAllUsers() {
        //Given
        List<User> userList = new ArrayList<>(List.of(new User()));
        given(userRepository.findAll()).willReturn(userList);

        //When
        var expected = userService.getAllUsers();

        //Then
        assertEquals(expected.size(), userList.size());
        verify(userRepository, atLeastOnce()).findAll();
    }

    @Test
    void itShouldGetByIdUser() {
        //Given
        User user = new User();
        user.setId(1L);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(modelMapper.map(user, UserDto.class)).willReturn(UserDto.builder().id(1L).build());

        //When
        var expected = userService.findById(user.getId());

        //Then
        assertTrue(expected.isPresent());
        verify(userRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    void itShouldGetByUsername() {
        //Given
        User user = new User();
        user.setUsername("rodolfo");

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(modelMapper.map(user, UserDto.class)).willReturn(UserDto.builder().username("rodolfo").build());

        //When
        var expected = userService.findByUsername(user.getUsername());

        //Then
        assertTrue(expected.isPresent());
        verify(userRepository, atLeastOnce()).findByUsername(anyString());
    }

    @Test
    void itShouldRegisterUser() {
        //Given
        UserRequestDto userRequestDto = UserRequestDto.builder().username("adam").build();

        UserDto userDto = UserDto.builder().username("adam").id(1L).build();

        User user = User.builder().username("adam").id(1L).build();

        given(modelMapper.map(userRequestDto, User.class)).willReturn(user);
        given(modelMapper.map(user, UserDto.class)).willReturn(userDto);
        given(userRepository.save(user)).willReturn(user);
        given(userRepository.existsByUsernameIgnoreCase(user.getUsername())).willReturn(false);


        //When
        var expected = userService.register(userRequestDto);

        //Then
        assertNotNull(expected);
        assertEquals(expected.get().getUsername(), userDto.getUsername());

        verify(userRepository, atLeastOnce()).save(user);
        verify(modelMapper, atLeastOnce()).map(user, UserDto.class);
        verify(modelMapper, atLeastOnce()).map(userRequestDto, User.class);
    }

    @Test
    void itShouldGetAllFollowingPostsByUserWithUserId() {
        //Given
        Post post = Post.builder().id(1L).content("test").build();
        PostDto postDto = PostDto.builder().id(post.getId()).build();
        Set<Post> posts = new HashSet<>(List.of(post));
        User user = User.builder().username("adam").id(1L).bio("old bio").followedPosts(posts).build();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(modelMapper.map(post, PostDto.class)).willReturn(postDto);

        //When
        var expected = userService.getAllFollowingPostsByUserWithUserId(user.getId());

        //Then
        assertEquals(expected.size(), posts.size());
        verify(userRepository, times(1)).findById(user.getId());
        verify(modelMapper, times(1)).map(post, PostDto.class);
    }

    @Test
    void itShouldChangeBio() {
        //Given
        User user = User.builder().username("adam").id(1L).bio("old bio").build();
        UserDto userDto = UserDto.builder().username("adam").id(1L).bio("new bio").build();
        BioSimpleDto bioSimpleDto = BioSimpleDto.builder().newBio("new bio").build();

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(modelMapper.map(user, UserDto.class)).willReturn(userDto);
        given(userRepository.save(user)).willReturn(user);

        //When
        var expected = userService.changeBio(user.getUsername(), bioSimpleDto);

        //Then
        assertNotNull(expected);
        assertEquals(expected.getBio(), bioSimpleDto.getNewBio());

        verify(userRepository, atLeastOnce()).save(user);
        verify(modelMapper, atLeastOnce()).map(user, UserDto.class);
    }


    @Test
    void itShouldNotReturnAvatar() throws IOException {
        //Given
        User user = User.builder().username("Darek").id(1L).build();
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        //When
        var expected = userService.getAvatar(user.getUsername());

        //Then
        assertEquals("Avatar not found", expected);
        verify(userRepository, atLeastOnce()).findByUsername(user.getUsername());
    }

    @Test
    void itShouldDeleteAvatar() throws IOException {
        //Given
        User user = User.builder().username("Darek").id(1L).build();
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        //When
        var expected = userService.deleteAvatar(user.getUsername());

        //Then
        assertEquals("Avatar deleted", expected);
        verify(userRepository, atLeastOnce()).findByUsername(user.getUsername());
    }

    @Test
    void itShouldDeleteUser() {
        //Given
        User user = User.builder().username("Darek").id(1L).accountStatus(AccountStatus.ACTIVE).build();
        UserDto userDto = UserDto.builder().username("Darek").id(1L).accountStatus(AccountStatus.DELETED).build();
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(modelMapper.map(user, UserDto.class)).willReturn(userDto);

        //When
        var expected = userService.deleteUser(user.getUsername());

        //Then
        assertEquals(AccountStatus.DELETED, expected.getAccountStatus());
        verify(userRepository, atLeastOnce()).findByUsername(user.getUsername());
    }

    @Test
    void itShouldDeleteUserPermanently() {
        //Given
        User user = User.builder().username("Darek").id(1L).accountStatus(AccountStatus.ACTIVE).build();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.deleteByUsernameIgnoreCase(user.getUsername())).willReturn(1);

        //When
        var expected = userService.deleteUserPermanently(user.getId());

        //Then
        assertEquals(true, expected);
        verify(userRepository, atLeastOnce()).findById(user.getId());
    }


}
